package com.imfine.ngs.crawler.steam.service;

import com.imfine.ngs.crawler.steam.client.SteamApiClient;
import com.imfine.ngs.crawler.steam.dto.SteamApiResponse;
import com.imfine.ngs.crawler.steam.dto.SteamGameDto;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.GameMainMedia;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.GameMainMediaRepository;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Steam 크롤링 서비스.
 * Steam API에서 데이터를 가져와 DB에 저장합니다.
 *
 * @author chan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SteamCrawlerService {

    private final SteamApiClient steamApiClient;
    private final GameRepository gameRepository;
    private final GameMainMediaRepository gameMainMediaRepository;

    /**
     * Steam 게임 크롤링 및 저장
     *
     * @param steamAppId Steam App ID
     * @return 저장된 게임 ID
     */
    @Transactional
    public Long crawlAndSaveGame(Integer steamAppId) {
        log.info("Starting crawl for Steam appId: {}", steamAppId);

        // 1. Steam API 호출
        SteamApiResponse response = steamApiClient.getGameDetails(steamAppId);

        if (response == null || response.getData() == null) {
            log.error("Failed to get data from Steam API for appId: {}", steamAppId);
            throw new RuntimeException("Failed to fetch data from Steam API");
        }

        // 2. Steam 응답 → SteamGameDto 변환
        SteamGameDto steamGameDto = convertToSteamGameDto(response.getData());

        // 3. Game 엔티티 생성 또는 업데이트
        Game game = createOrUpdateGame(steamGameDto);

        // 4. GameMainMedia 저장 (스크린샷, 비디오)
        saveGameMedia(game, steamGameDto);

        log.info("Successfully crawled and saved game: {} (appId: {})", game.getName(), steamAppId);
        return game.getId();
    }

    /**
     * Steam API 응답 → SteamGameDto 변환
     */
    private SteamGameDto convertToSteamGameDto(SteamApiResponse.SteamGameData data) {
        // 스크린샷 URL 추출
        List<String> screenshotUrls = new ArrayList<>();
        if (data.getScreenshots() != null) {
            screenshotUrls = data.getScreenshots().stream()
                    .map(SteamApiResponse.Screenshot::getPathFull)
                    .collect(Collectors.toList());
        }

        // 비디오 URL 추출 (MP4 480p 우선)
        List<String> videoUrls = new ArrayList<>();
        if (data.getMovies() != null) {
            videoUrls = data.getMovies().stream()
                    .map(movie -> {
                        if (movie.getMp4() != null && movie.getMp4().get("480") != null) {
                            return movie.getMp4().get("480");
                        }
                        return null;
                    })
                    .filter(url -> url != null)
                    .collect(Collectors.toList());
        }

        // 가격 (원화, Steam API는 센트 단위로 반환)
        Long price = null;
        if (data.getPriceOverview() != null) {
            price = data.getPriceOverview().getFinalPrice() / 100L; // 센트 → 원
        }

        // 장르 추출
        List<String> genres = new ArrayList<>();
        if (data.getGenres() != null) {
            genres = data.getGenres().stream()
                    .map(SteamApiResponse.Genre::getDescription)
                    .collect(Collectors.toList());
        }

        // PC 사양 정보 추출
        String spec = null;
        if (data.getPcRequirements() != null) {
            StringBuilder specBuilder = new StringBuilder();
            if (data.getPcRequirements().getMinimum() != null) {
                specBuilder.append("최소 사양:\n").append(data.getPcRequirements().getMinimum()).append("\n\n");
            }
            if (data.getPcRequirements().getRecommended() != null) {
                specBuilder.append("권장 사양:\n").append(data.getPcRequirements().getRecommended());
            }
            spec = specBuilder.toString();
        }

        return SteamGameDto.builder()
                .steamAppId(data.getSteamAppid())
                .name(data.getName())
                .price(price != null ? price : 0L)
                .description(data.getDetailedDescription())
                .introduction(data.getShortDescription())
                .thumbnailUrl(data.getHeaderImage())
                .screenshotUrls(screenshotUrls)
                .videoUrls(videoUrls)
                .genres(genres)
                .spec(spec)
                .releaseDate(data.getReleaseDate() != null ? data.getReleaseDate().getDate() : null)
                .build();
    }

    /**
     * Game 엔티티 생성 또는 업데이트
     */
    private Game createOrUpdateGame(SteamGameDto steamGameDto) {
        // Steam App ID로 기존 게임 조회는 현재 불가 (Game 엔티티에 steamAppId 필드 없음)
        // 따라서 항상 새로운 게임으로 생성

        // description, introduction, spec 길이 제한 (DB 컬럼 크기에 맞춤)
        String description = truncateString(steamGameDto.getDescription(), 5000);
        String introduction = truncateString(steamGameDto.getIntroduction(), 1000);
        String spec = truncateString(steamGameDto.getSpec(), 3000);

        log.info("Saving game: {} (price: {})", steamGameDto.getName(), steamGameDto.getPrice());

        Game game = Game.builder()
                .name(steamGameDto.getName())
                .price(steamGameDto.getPrice())
                .description(description)
                .introduction(introduction)
                .spec(spec)
                .thumbnailUrl(steamGameDto.getThumbnailUrl())
                .gameStatus(GameStatusType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return gameRepository.save(game);
    }

    /**
     * 문자열 길이 제한
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    /**
     * GameMainMedia 저장 (스크린샷, 비디오)
     */
    private void saveGameMedia(Game game, SteamGameDto steamGameDto) {
        // 기존 미디어 삭제 (업데이트 시)
        gameMainMediaRepository.deleteByGameId(game.getId());

        List<GameMainMedia> mediaList = new ArrayList<>();

        // 스크린샷 추가
        if (steamGameDto.getScreenshotUrls() != null) {
            for (String url : steamGameDto.getScreenshotUrls()) {
                GameMainMedia media = GameMainMedia.builder()
                        .game(game)
                        .fileUrl(url)
                        .build();
                mediaList.add(media);
            }
        }

        // 비디오 추가
        if (steamGameDto.getVideoUrls() != null) {
            for (String url : steamGameDto.getVideoUrls()) {
                GameMainMedia media = GameMainMedia.builder()
                        .game(game)
                        .fileUrl(url)
                        .build();
                mediaList.add(media);
            }
        }

        if (!mediaList.isEmpty()) {
            gameMainMediaRepository.saveAll(mediaList);
            log.info("Saved {} media files for game: {}", mediaList.size(), game.getName());
        }
    }

    /**
     * 여러 게임 일괄 크롤링
     *
     * @param steamAppIds Steam App ID 목록
     * @return 저장된 게임 ID 목록
     */
    @Transactional
    public List<Long> crawlAndSaveGames(List<Integer> steamAppIds) {
        List<Long> gameIds = new ArrayList<>();

        for (Integer appId : steamAppIds) {
            try {
                Long gameId = crawlAndSaveGame(appId);
                gameIds.add(gameId);

                // Steam API Rate Limit 방지를 위한 딜레이 (1초)
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("Failed to crawl appId: {}", appId, e);
                // 계속 진행
            }
        }

        log.info("Bulk crawl completed. Successfully crawled {} out of {} games",
                gameIds.size(), steamAppIds.size());

        return gameIds;
    }

}

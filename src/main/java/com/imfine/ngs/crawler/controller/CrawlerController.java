package com.imfine.ngs.crawler.controller;

import com.imfine.ngs.crawler.steam.service.SteamCrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 크롤링 관리자 API 컨트롤러.
 * Steam 게임 데이터를 크롤링하여 DB에 저장합니다.
 *
 * @author chan
 */
@Slf4j
@Tag(name = "Crawler (Admin)", description = "관리자 전용 크롤링 API")
@RequiredArgsConstructor
@RequestMapping("/api/admin/crawl")
@RestController
public class CrawlerController {

    private final SteamCrawlerService steamCrawlerService;

    /**
     * Steam 게임 단일 크롤링
     *
     * @param steamAppId Steam App ID
     * @return 저장된 게임 ID
     */
    @Operation(
            summary = "Steam 게임 크롤링",
            description = "Steam App ID로 게임 정보를 크롤링하여 DB에 저장합니다."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/steam/game/{steamAppId}")
    public Map<String, Object> crawlSteamGame(
            @Parameter(description = "Steam App ID", required = true, example = "1245620")
            @PathVariable Integer steamAppId) {

        log.info("Received crawl request for Steam appId: {}", steamAppId);

        Long gameId = steamCrawlerService.crawlAndSaveGame(steamAppId);

        return Map.of(
                "success", true,
                "message", "Game crawled successfully",
                "gameId", gameId,
                "steamAppId", steamAppId
        );
    }

    /**
     * Steam 게임 일괄 크롤링
     *
     * @param request Steam App ID 목록
     * @return 저장된 게임 ID 목록
     */
    @Operation(
            summary = "Steam 게임 일괄 크롤링",
            description = "여러 게임을 한 번에 크롤링합니다. (주의: API 제한으로 인해 게임당 1초 딜레이 발생)"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/steam/bulk")
    public Map<String, Object> crawlSteamGamesBulk(
            @RequestBody BulkCrawlRequest request) {

        log.info("Received bulk crawl request for {} games", request.getSteamAppIds().size());

        List<Long> gameIds = steamCrawlerService.crawlAndSaveGames(request.getSteamAppIds());

        return Map.of(
                "success", true,
                "message", "Bulk crawl completed",
                "totalRequested", request.getSteamAppIds().size(),
                "totalCrawled", gameIds.size(),
                "gameIds", gameIds
        );
    }

    /**
     * 크롤링 상태 조회 (추후 구현)
     *
     * @return 크롤링 상태 정보
     */
    @Operation(
            summary = "크롤링 상태 조회",
            description = "현재 크롤링 상태 및 통계 정보를 조회합니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/status")
    public Map<String, Object> getCrawlStatus() {
        // TODO: 크롤링 상태 관리 기능 구현
        return Map.of(
                "status", "idle",
                "message", "No active crawling task"
        );
    }

    /**
     * 일괄 크롤링 요청 DTO
     */
    public static class BulkCrawlRequest {
        private List<Integer> steamAppIds;

        public BulkCrawlRequest() {}

        public BulkCrawlRequest(List<Integer> steamAppIds) {
            this.steamAppIds = steamAppIds;
        }

        public List<Integer> getSteamAppIds() {
            return steamAppIds;
        }

        public void setSteamAppIds(List<Integer> steamAppIds) {
            this.steamAppIds = steamAppIds;
        }
    }
}

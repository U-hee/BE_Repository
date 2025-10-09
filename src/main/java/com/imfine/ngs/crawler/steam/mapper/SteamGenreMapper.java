package com.imfine.ngs.crawler.steam.mapper;

import com.imfine.ngs.game.enums.GameTagType;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Steam API의 genre를 GameTagType enum으로 매핑하는 유틸리티 클래스
 *
 * @author chan
 */
@Slf4j
public class SteamGenreMapper {

    /**
     * Steam genre description → GameTagType 매핑 테이블
     * 대소문자 구분 없이 매핑
     */
    private static final Map<String, GameTagType> GENRE_MAPPING = new HashMap<>();

    static {
        // 기본 장르
        GENRE_MAPPING.put("action", GameTagType.ACTION);
        GENRE_MAPPING.put("rpg", GameTagType.RPG);
        GENRE_MAPPING.put("strategy", GameTagType.STRATEGY);
        GENRE_MAPPING.put("simulation", GameTagType.SIMULATION);
        GENRE_MAPPING.put("sports", GameTagType.SPORTS);
        GENRE_MAPPING.put("racing", GameTagType.RACING);
        GENRE_MAPPING.put("puzzle", GameTagType.PUZZLE);
        GENRE_MAPPING.put("adventure", GameTagType.ADVENTURE);
        GENRE_MAPPING.put("shooter", GameTagType.SHOOTER);
        GENRE_MAPPING.put("fighting", GameTagType.FIGHTING);
        GENRE_MAPPING.put("platformer", GameTagType.PLATFORMER);
        GENRE_MAPPING.put("horror", GameTagType.HORROR);
        GENRE_MAPPING.put("indie", GameTagType.INDIE);
        GENRE_MAPPING.put("casual", GameTagType.CASUAL);
        GENRE_MAPPING.put("mmo", GameTagType.MMORPG);
        GENRE_MAPPING.put("mmorpg", GameTagType.MMORPG);
        GENRE_MAPPING.put("survival", GameTagType.SURVIVAL);
        GENRE_MAPPING.put("sandbox", GameTagType.SANDBOX);
        GENRE_MAPPING.put("educational", GameTagType.EDUCATIONAL);

        // Steam 특화 장르
        GENRE_MAPPING.put("massively multiplayer", GameTagType.MASSIVELY_MULTIPLAYER);
        GENRE_MAPPING.put("free to play", GameTagType.FREE_TO_PLAY);
        GENRE_MAPPING.put("early access", GameTagType.EARLY_ACCESS);
        GENRE_MAPPING.put("visual novel", GameTagType.VISUAL_NOVEL);
        GENRE_MAPPING.put("card game", GameTagType.CARD_GAME);
        GENRE_MAPPING.put("tower defense", GameTagType.TOWER_DEFENSE);
        GENRE_MAPPING.put("stealth", GameTagType.STEALTH);
        GENRE_MAPPING.put("roguelike", GameTagType.ROGUELIKE);
        GENRE_MAPPING.put("roguelite", GameTagType.ROGUELIKE);
        GENRE_MAPPING.put("open world", GameTagType.OPEN_WORLD);
        GENRE_MAPPING.put("multiplayer", GameTagType.MULTIPLAYER);
        GENRE_MAPPING.put("co-op", GameTagType.CO_OP);
        GENRE_MAPPING.put("coop", GameTagType.CO_OP);
        GENRE_MAPPING.put("cooperative", GameTagType.CO_OP);

        // 한국어 장르 매핑
        GENRE_MAPPING.put("액션", GameTagType.ACTION);
        GENRE_MAPPING.put("전략", GameTagType.STRATEGY);
        GENRE_MAPPING.put("시뮬레이션", GameTagType.SIMULATION);
        GENRE_MAPPING.put("스포츠", GameTagType.SPORTS);
        GENRE_MAPPING.put("레이싱", GameTagType.RACING);
        GENRE_MAPPING.put("퍼즐", GameTagType.PUZZLE);
        GENRE_MAPPING.put("어드벤처", GameTagType.ADVENTURE);
        GENRE_MAPPING.put("슈팅", GameTagType.SHOOTER);
        GENRE_MAPPING.put("격투", GameTagType.FIGHTING);
        GENRE_MAPPING.put("플랫포머", GameTagType.PLATFORMER);
        GENRE_MAPPING.put("공포", GameTagType.HORROR);
        GENRE_MAPPING.put("호러", GameTagType.HORROR);
        GENRE_MAPPING.put("인디", GameTagType.INDIE);
        GENRE_MAPPING.put("캐주얼", GameTagType.CASUAL);
        GENRE_MAPPING.put("생존", GameTagType.SURVIVAL);
        GENRE_MAPPING.put("샌드박스", GameTagType.SANDBOX);
        GENRE_MAPPING.put("교육", GameTagType.EDUCATIONAL);
        GENRE_MAPPING.put("대규모 멀티플레이어", GameTagType.MASSIVELY_MULTIPLAYER);
        GENRE_MAPPING.put("무료 플레이", GameTagType.FREE_TO_PLAY);
        GENRE_MAPPING.put("무료", GameTagType.FREE_TO_PLAY);
        GENRE_MAPPING.put("앞서 해보기", GameTagType.EARLY_ACCESS);
        GENRE_MAPPING.put("비주얼 노벨", GameTagType.VISUAL_NOVEL);
        GENRE_MAPPING.put("카드 게임", GameTagType.CARD_GAME);
        GENRE_MAPPING.put("타워 디펜스", GameTagType.TOWER_DEFENSE);
        GENRE_MAPPING.put("스텔스", GameTagType.STEALTH);
        GENRE_MAPPING.put("로그라이크", GameTagType.ROGUELIKE);
        GENRE_MAPPING.put("오픈 월드", GameTagType.OPEN_WORLD);
        GENRE_MAPPING.put("멀티플레이어", GameTagType.MULTIPLAYER);
        GENRE_MAPPING.put("협동", GameTagType.CO_OP);
    }

    /**
     * Steam genre 리스트를 GameTagType 리스트로 변환
     *
     * @param steamGenres Steam API에서 받은 genre 문자열 리스트
     * @return 변환된 GameTagType 리스트 (중복 제거, 매핑 실패한 것은 제외)
     */
    public static List<GameTagType> mapGenresToTags(List<String> steamGenres) {
        if (steamGenres == null || steamGenres.isEmpty()) {
            log.debug("No genres provided for mapping");
            return new ArrayList<>();
        }

        List<GameTagType> mappedTags = new ArrayList<>();

        for (String genre : steamGenres) {
            if (genre == null || genre.trim().isEmpty()) {
                continue;
            }

            // 대소문자 무시, 공백 trim
            String normalizedGenre = genre.toLowerCase().trim();

            GameTagType tagType = GENRE_MAPPING.get(normalizedGenre);

            if (tagType != null) {
                // 중복 방지
                if (!mappedTags.contains(tagType)) {
                    mappedTags.add(tagType);
                    log.debug("Mapped Steam genre '{}' → {}", genre, tagType);
                }
            } else {
                log.warn("Unknown Steam genre: '{}' - skipping", genre);
            }
        }

        log.info("Mapped {} out of {} Steam genres to tags", mappedTags.size(), steamGenres.size());
        return mappedTags;
    }

    /**
     * 단일 Steam genre를 GameTagType으로 변환
     *
     * @param steamGenre Steam API에서 받은 genre 문자열
     * @return 변환된 GameTagType, 매핑 실패 시 null
     */
    public static GameTagType mapGenreToTag(String steamGenre) {
        if (steamGenre == null || steamGenre.trim().isEmpty()) {
            return null;
        }

        String normalizedGenre = steamGenre.toLowerCase().trim();
        GameTagType tagType = GENRE_MAPPING.get(normalizedGenre);

        if (tagType == null) {
            log.warn("Unknown Steam genre: '{}' - returning null", steamGenre);
        }

        return tagType;
    }
}

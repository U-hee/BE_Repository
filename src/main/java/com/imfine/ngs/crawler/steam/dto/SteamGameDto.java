package com.imfine.ngs.crawler.steam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Steam 게임 데이터 DTO (내부 처리용).
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SteamGameDto {

    private Integer steamAppId;
    private String name;
    private Long price;
    private String description;
    private String introduction;
    private String thumbnailUrl;
    private String spec;  // 게임 사양 (시스템 요구사항)
    private List<String> screenshotUrls;
    private List<String> videoUrls;
    private List<String> tags;
    private List<String> genres;
    private List<String> platforms;
    private String releaseDate;
}

package com.imfine.ngs.crawler.steam.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Steam API 응답 DTO.
 * https://store.steampowered.com/api/appdetails?appids={appid}
 *
 * @author chan
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamApiResponse {

    private Boolean success;
    private SteamGameData data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SteamGameData {

        @JsonProperty("steam_appid")
        private Integer steamAppid;

        private String name;

        @JsonProperty("detailed_description")
        private String detailedDescription;

        @JsonProperty("short_description")
        private String shortDescription;

        @JsonProperty("header_image")
        private String headerImage;

        private List<Screenshot> screenshots;

        private List<Movie> movies;

        @JsonProperty("price_overview")
        private PriceOverview priceOverview;

        private List<Category> categories;

        private List<Genre> genres;

        @JsonProperty("release_date")
        private ReleaseDate releaseDate;

        private Map<String, Boolean> platforms;

        @JsonProperty("pc_requirements")
        private PcRequirements pcRequirements;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Screenshot {
        private Integer id;

        @JsonProperty("path_thumbnail")
        private String pathThumbnail;

        @JsonProperty("path_full")
        private String pathFull;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Movie {
        private Integer id;
        private String name;
        private String thumbnail;

        private Map<String, String> webm;
        private Map<String, String> mp4;

        @JsonProperty("highlight")
        private Boolean highlight;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceOverview {
        private String currency;

        @JsonProperty("initial")
        private Integer initial;

        @JsonProperty("final")
        private Integer finalPrice;

        @JsonProperty("discount_percent")
        private Integer discountPercent;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        private Integer id;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Genre {
        private String id;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseDate {
        @JsonProperty("coming_soon")
        private Boolean comingSoon;

        private String date;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PcRequirements {
        private String minimum;
        private String recommended;
    }
}

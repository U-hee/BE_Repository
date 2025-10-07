package com.imfine.ngs.crawler.steam.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imfine.ngs.crawler.steam.dto.SteamApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Steam API 클라이언트.
 * Steam Store API를 호출하여 게임 정보를 가져옵니다.
 *
 * @author chan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SteamApiClient {

    private static final String STEAM_API_URL = "https://store.steampowered.com/api/appdetails";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Steam API에서 게임 상세 정보 조회
     *
     * @param appId Steam App ID
     * @return Steam API 응답
     */
    public SteamApiResponse getGameDetails(Integer appId) {
        try {
            String url = STEAM_API_URL + "?appids=" + appId + "&cc=kr&l=korean";

            log.info("Calling Steam API for appId: {}", appId);

            String response = restTemplate.getForObject(url, String.class);

            // Steam API는 응답을 {appId: {success: true, data: {...}}} 형태로 반환
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode appNode = rootNode.get(appId.toString());

            if (appNode == null) {
                log.error("No data found for appId: {}", appId);
                return null;
            }

            SteamApiResponse steamResponse = objectMapper.treeToValue(appNode, SteamApiResponse.class);

            if (Boolean.FALSE.equals(steamResponse.getSuccess())) {
                log.warn("Steam API returned success=false for appId: {}", appId);
                return null;
            }

            log.info("Successfully fetched game data for appId: {}", appId);
            return steamResponse;

        } catch (Exception e) {
            log.error("Failed to fetch game details from Steam API for appId: {}", appId, e);
            return null;
        }
    }

    /**
     * 여러 게임의 앱 ID 목록 조회 (Steam Apps List API)
     * 참고: 실제 사용 시에는 필요한 앱 ID만 선별하여 사용
     *
     * @return 앱 ID 리스트
     */
    public Map<String, Object> getAppList() {
        try {
            String url = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.info("Successfully fetched Steam app list");
            return response;

        } catch (Exception e) {
            log.error("Failed to fetch Steam app list", e);
            return null;
        }
    }
}

package com.imfine.ngs._global.config.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS 설정을 외부 프로퍼티에서 주입받는 Record 클래스
 * application.yml의 cors 섹션과 매핑된다.
 *
 * @param allowedOrigins 허용할 Origin 목록 (도메인)
 * @param allowedMethods 허용할 HTTP 메서드 목록
 * @param allowedHeaders 허용할 헤더 목록
 * @param allowCredentials 인증 정보 포함 허용 여부
 * @param maxAge Preflight 요청 캐시 시간 (초)
 *
 * @author gimtaehui
 */
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        List<String> allowedOrigins,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        boolean allowCredentials,
        long maxAge
) {}
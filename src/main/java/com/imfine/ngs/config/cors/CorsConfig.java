package com.imfine.ngs.config.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS 설정을 관리하는 Configuration 클래스
 * CorsProperties와 함께 CORS Bean을 생성하고 관리한다.
 *
 * @author gimtaehui
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
@RequiredArgsConstructor
public class CorsConfig {

  private final CorsProperties corsProperties;

  /**
   * CORS 설정을 정의한다.
   * application.yml의 cors 섹션에서 설정값을 가져와 적용한다.
   *
   * @return CORS 설정 소스
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 프로퍼티에서 CORS 설정 가져오기
    configuration.setAllowedOrigins(corsProperties.allowedOrigins());
    configuration.setAllowedMethods(corsProperties.allowedMethods());
    configuration.setAllowedHeaders(corsProperties.allowedHeaders());
    configuration.setAllowCredentials(corsProperties.allowCredentials());
    configuration.setMaxAge(corsProperties.maxAge());

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
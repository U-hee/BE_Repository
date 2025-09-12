package com.imfine.ngs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * OpenAPI 설정 클래스
 * <p>
 * Swagger UI 문서 생성을 위한 설정 및 루트 경로 리디렉트
 * 
 * @author XIYO
 * @since 2025-08-02
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI memberCampusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Member Campus Study API")
                        .description("JPA와 MyBatis 쿼리 학습을 위한 회원 관리 API")
                        .version("1.0.0")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버")));
    }

    /**
     * 루트 경로(/)를 Swagger UI로 리디렉트
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }
}
package com.imfine.ngs._global.config.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 설정 클래스.
 *
 * @author chan
 */
@Configuration
public class QueryDslConfig {

    // entityManager
    @PersistenceContext
    private EntityManager entityManager;

    // JpaQueryFactory
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}

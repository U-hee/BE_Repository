package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.dto.request.GameSearchRequest;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.QGame;
import com.imfine.ngs.game.entity.tag.QGameTag;
import com.imfine.ngs.game.entity.tag.QLinkedTag;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.enums.SortType;
import com.imfine.ngs.game.repository.queryDsl.GameRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link com.imfine.ngs.game.repository.queryDsl.GameRepositoryCustom} 구현체
 * QueryDsl을 사용한 동적 쿼리 작성
 *
 * 주의: 클래스 이름은 반드시 {Repository명}Impl 형식이어야 합니다.
 *
 * @author chan
 */
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Game> searchGames(GameSearchRequest request, Pageable pageable) {
        QGame game = QGame.game;
        QLinkedTag linkedTag = QLinkedTag.linkedTag;
        QGameTag gameTag = QGameTag.gameTag;

        // 게임 목록 조회 (페이징 적용)
        List<Game> content = jpaQueryFactory
                .selectFrom(game)
                .distinct()
                .leftJoin(game.publisher).fetchJoin()  // N+1 방지
                .leftJoin(game.tags, linkedTag)
                .leftJoin(linkedTag.gameTag, gameTag)
                .where(
                        game.gameStatus.eq(GameStatusType.ACTIVE),
                        nameContains(request.getName()),
                        priceBetween(request.getMinPrice(), request.getMaxPrice()),
                        ratingBetween(request.getMinRating(), request.getMaxRating()),
                        tagEquals(request.getTag())
                )
                .orderBy(getOrderSpecifier(request.getSortType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 (count 쿼리)
        Long total = jpaQueryFactory
                .select(game.countDistinct())
                .from(game)
                .leftJoin(game.tags, linkedTag)
                .leftJoin(linkedTag.gameTag, gameTag)
                .where(
                        game.gameStatus.eq(GameStatusType.ACTIVE),
                        nameContains(request.getName()),
                        priceBetween(request.getMinPrice(), request.getMaxPrice()),
                        ratingBetween(request.getMinRating(), request.getMaxRating()),
                        tagEquals(request.getTag())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 게임 이름 검색 조건 (부분 일치, 대소문자 무시)
     */
    private BooleanExpression nameContains(String name) {
        return (name != null && !name.trim().isEmpty())
                ? QGame.game.name.lower().contains(name.toLowerCase())
                : null;
    }

    /**
     * 가격 범위 검색 조건
     */
    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return QGame.game.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return QGame.game.price.goe(minPrice);  // greater or equal
        } else if (maxPrice != null) {
            return QGame.game.price.loe(maxPrice);  // less or equal
        }
        return null;
    }

    /**
     * 평점 범위 검색 조건
     *
     * QueryDSL의 서브쿼리 제약으로 인해 평점 필터링은 애플리케이션 레벨에서 처리합니다.
     *
     * 대안:
     * 1. Game 엔티티에 avgRating 컬럼 추가 (권장) - 성능 최적화
     * 2. Native Query 사용 - 동적 쿼리 장점 상실
     * 3. 애플리케이션에서 후처리 - 현재 방식, 페이징 이슈 있음
     */
    private BooleanExpression ratingBetween(Double minRating, Double maxRating) {
        // QueryDSL WHERE 절에서는 서브쿼리 결과를 직접 비교할 수 없음
        // 평점 필터링이 필요한 경우 Service 레이어에서 처리하거나
        // Game 엔티티에 avgRating 필드를 추가하는 것을 권장
        return null;
    }

    /**
     * 태그 검색 조건 (단일 태그)
     */
    private BooleanExpression tagEquals(GameTagType tag) {
        return tag != null ? QGameTag.gameTag.tagType.eq(tag) : null;
    }

    /**
     * 정렬 조건 생성
     */
    private OrderSpecifier<?> getOrderSpecifier(SortType sortType) {
        QGame game = QGame.game;

        if (sortType == null) {
            sortType = SortType.DATE_DESC;  // 기본값: 최신순
        }

        return switch (sortType) {
            case PRICE_ASC -> game.price.asc();
            case PRICE_DESC -> game.price.desc();
            case DATE_ASC -> game.createdAt.asc();
            case DATE_DESC -> game.createdAt.desc();
            case NAME_ASC -> game.name.asc();
            case NAME_DESC -> game.name.desc();
            // 평점과 리뷰수 정렬은 서브쿼리를 사용할 수 없으므로 기본 정렬 사용
            case RATING_DESC, REVIEW_COUNT_DESC -> game.createdAt.desc();
        };
    }
}

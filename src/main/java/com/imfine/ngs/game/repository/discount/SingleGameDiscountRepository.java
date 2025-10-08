package com.imfine.ngs.game.repository.discount;

import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.enums.GameStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@link SingleGameDiscount} Repository
 */
public interface SingleGameDiscountRepository extends JpaRepository<SingleGameDiscount, Long> {

    /**
     * 할인율 범위로 활성 할인 게임 조회
     *
     * @param minRate  최소 할인율 (null 가능)
     * @param maxRate  최대 할인율 (null 가능)
     * @param now      현재 시간
     * @param status   게임 상태
     * @param pageable 페이징
     * @return 활성 할인 정보
     */
    @Query("""
            SELECT d FROM SingleGameDiscount d
            WHERE d.game.gameStatus = :status
            AND d.createdAt <= :now
            AND d.expiresAt > :now
            AND (:minRate IS NULL OR d.discountRate >= :minRate)
            AND (:maxRate IS NULL OR d.discountRate <= :maxRate)
            ORDER BY d.discountRate DESC
            """)
    Page<SingleGameDiscount> findActiveDiscountGames(
            @Param("minRate") BigDecimal minRate,
            @Param("maxRate") BigDecimal maxRate,
            @Param("now") LocalDateTime now,
            @Param("status") GameStatusType status,
            Pageable pageable
    );
}

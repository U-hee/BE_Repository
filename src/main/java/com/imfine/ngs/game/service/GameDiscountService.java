package com.imfine.ngs.game.service;

import com.imfine.ngs.game.dto.mapper.GameCardMapper;
import com.imfine.ngs.game.dto.request.GameDiscountRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.entity.discount.SingleGameDiscount;
import com.imfine.ngs.game.enums.GameStatusType;
import com.imfine.ngs.game.repository.discount.SingleGameDiscountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * {@link com.imfine.ngs.game.entity.Game}의 할인율 응답 비즈니스 로직 클래스.
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GameDiscountService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final SingleGameDiscountRepository discountRepository;
    private final GameCardMapper gameCardMapper;

    /**
     * 할인율 범위로 게임 조회 (0~100)
     *
     * @param request  할인율 검색 조건 (minDiscount, maxDiscount)
     * @param pageable 페이징 정보
     * @return 할인 게임 목록
     */
    public Page<GameCardResponse> getDiscountGame(GameDiscountRequest request, Pageable pageable) {

        // 1. 할인율을 BigDecimal로 변환 (null 허용)
        BigDecimal minRate = request.getMinDiscount() != null
                ? new BigDecimal(request.getMinDiscount())
                : null;

        BigDecimal maxRate = request.getMaxDiscount() != null
                ? new BigDecimal(request.getMaxDiscount())
                : null;

        log.debug("Discount query - minDiscount: {}, maxDiscount: {}, minRate: {}, maxRate: {}",
                request.getMinDiscount(), request.getMaxDiscount(), minRate, maxRate);

        LocalDateTime now = LocalDateTime.now();

        // 2. Repository에서 활성 할인 게임 조회
        Page<SingleGameDiscount> discounts = discountRepository.findActiveDiscountGames(
                minRate,
                maxRate,
                now,
                GameStatusType.ACTIVE,
                pageable
        );

        // 3. 할인가 적용된 응답으로 변환
        return discounts.map(this::toDiscountedResponse);
    }

    /**
     * SingleGameDiscount를 할인가가 적용된 GameCardResponse로 변환
     *
     * @param discount 할인 정보
     * @return 할인가가 적용된 게임 카드 응답
     */
    private GameCardResponse toDiscountedResponse(SingleGameDiscount discount) {

        Game game = discount.getGame();

        // 할인가 계산
        Long discountedPrice = calculateDiscountedPrice(
                game.getPrice(),
                discount.getDiscountRate()
        );

        log.debug("Discounted price for discount {}", discountedPrice);

        // Mapper를 통해 할인 정보 적용된 응답 생성
        return gameCardMapper.toCardResponseWithDiscount(
                game,
                discountedPrice,
                discount.getDiscountRate().intValue()
        );
    }

    /**
     * 할인가 계산 (반올림 처리)
     *
     * @param originalPrice 원가
     * @param discountRate  할인율 (0~100)
     * @return 할인가
     */
    private Long calculateDiscountedPrice(Long originalPrice, BigDecimal discountRate) {
        if (discountRate == null || discountRate.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        // 할인율 비율 = 할인율 / 100
        BigDecimal rateRatio = discountRate.divide(HUNDRED, 4, RoundingMode.HALF_UP);

        // 할인 금액 = 원가 × 할인율 비율
        BigDecimal discountAmount = new BigDecimal(originalPrice)
                .multiply(rateRatio)
                .setScale(0, RoundingMode.HALF_UP); // 원 단위 반올림

        log.debug("=== original price : {} === ", originalPrice);
        log.debug("=== discount rate : {} === ", discountRate);

        // 할인가 = 원가 - 할인 금액
        return originalPrice - discountAmount.longValue();
    }
}

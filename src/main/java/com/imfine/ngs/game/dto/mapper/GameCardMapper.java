package com.imfine.ngs.game.dto.mapper;

import com.imfine.ngs.game.dto.mapper.helper.GameMapperHelper;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.entity.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Game 엔티티를 GameCardResponse DTO로 변환하는 매퍼 클래스
 * 목록 조회용 응답 DTO 생성을 담당
 *
 * @author chan
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class GameCardMapper {

    private final GameMapperHelper helper;

    /**
     * Game 엔티티를 GameCardResponse DTO로 변환
     *
     * @param game 변환할 Game 엔티티
     * @return GameCardResponse DTO
     */
    public GameCardResponse toCardResponse(Game game) {
        if (game == null) {
            return null;
        }

        return GameCardResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .price(game.getPrice())
                .discountRate(helper.calculateCurrentDiscountRate(game.getDiscounts()))
                .tags(helper.extractTagNames(game.getTags()))
                .publisherId(game.getPublisher() != null ? game.getPublisher().getId() : null)
                .publisherName(game.getPublisher() != null ? game.getPublisher().getName() : null)
                .reviewCount(helper.calculateReviewCount(game.getReviews(), true))
                .averageScore(helper.calculateAverageScore(game.getReviews(), true))
                .releaseDate(game.getCreatedAt() != null ? game.getCreatedAt().toLocalDate() : null)
                .thumbnailUrl(game.getThumbnailUrl())
                .build();
    }

    /**
     * Game 엔티티를 할인가가 적용된 GameCardResponse로 변환
     *
     * @param game 변환할 Game 엔티티
     * @param discountedPrice 계산된 할인가
     * @param actualDiscountRate 실제 적용된 할인율
     * @return 할인 정보가 적용된 GameCardResponse DTO
     */
    public GameCardResponse toCardResponseWithDiscount(
            Game game,
            Long discountedPrice,
            Integer actualDiscountRate) {

        if (game == null) {
            return null;
        }

        // 기본 응답 생성
        GameCardResponse base = toCardResponse(game);

        // 할인 정보로 덮어쓰기
        return GameCardResponse.builder()
                .id(base.getId())
                .name(base.getName())
                .price(discountedPrice)  // 할인가로 교체
                .discountRate(actualDiscountRate)  // 실제 할인율로 교체
                .tags(base.getTags())
                .publisherId(base.getPublisherId())
                .publisherName(base.getPublisherName())
                .reviewCount(base.getReviewCount())
                .averageScore(base.getAverageScore())
                .releaseDate(base.getReleaseDate())
                .thumbnailUrl(base.getThumbnailUrl())
                .build();
    }
}

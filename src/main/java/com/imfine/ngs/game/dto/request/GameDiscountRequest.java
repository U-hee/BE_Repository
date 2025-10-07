package com.imfine.ngs.game.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@link com.imfine.ngs.game.entity.Game} 할인율 조회 요청 DTO 클래스.
 *
 * @author chan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDiscountRequest {

    /**
     * 최소 할인율 (0 ~ 100)
     */
    @Min(value = 0, message = "최소 할인율은 0 이상이어야 합니다")
    @Max(value = 100, message = "최소 할인율은 100 이하여야 합니다")
    private Integer minDiscount;

    /**
     * 최대 할인율 (0 ~ 100)
     */
    @Min(value = 0, message = "최대 할인율은 0 이상이어야 합니다")
    @Max(value = 100, message = "최대 할인율은 100 이하여야 합니다")
    private Integer maxDiscount;

    /**
     * 검증: minDiscount <= maxDiscount
     */
    @AssertTrue(message = "최소 할인율은 최대 할인율보다 작거나 같아야 합니다")
    public boolean isValidDiscountRange() {
        if (minDiscount == null || maxDiscount == null) {
            return true;
        }
        return minDiscount <= maxDiscount;
    }
}

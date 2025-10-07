package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.request.GameDiscountRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.service.GameDiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * {@link com.imfine.ngs.game.entity.Game}의 {@link com.imfine.ngs.game.entity.discount.SingleGameDiscount} 할인율 조회 응답 컨트롤러 클래스.
 *
 * @author chan
 */
@Tag(name = "Game Discount", description = "게임 할인 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/games/discount")
@RestController
public class GameDiscountController {

    private final GameDiscountService discountService;

    /**
     * 할인율 범위로 게임 조회
     *
     * @param request  할인율 검색 조건 (minDiscount, maxDiscount)
     * @param pageable 페이징 정보
     * @return 할인 게임 목록
     */
    @Operation(
            summary = "할인 게임 조회",
            description = "할인율 범위로 게임을 조회합니다. minDiscount, maxDiscount 모두 선택적 파라미터입니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<GameCardResponse> getDiscountGame(
            @Valid
            @Parameter(description = "할인율 검색 조건 (minDiscount: 최소 할인율, maxDiscount: 최대 할인율)")
            GameDiscountRequest request,
            @PageableDefault(size = 20) Pageable pageable) {

        return discountService.getDiscountGame(request, pageable);
    }
}

package com.imfine.ngs.game.controller;

import com.imfine.ngs.game.dto.request.GameSearchRequest;
import com.imfine.ngs.game.dto.response.GameCardResponse;
import com.imfine.ngs.game.dto.response.GameDetailResponse;
import com.imfine.ngs.game.enums.GameTagType;
import com.imfine.ngs.game.service.GameService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

/**
 * {@link com.imfine.ngs.game.entity.Game} 컨트롤러 클래스.
 * TODO: api를 보다 명확하게 하기 위해서 /api/games/search 로 변경하는 건 어떨까?
 *
 * @author chan
 */
@Tag(name = "Game", description = "게임 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/games")
@RestController
public class GameController {

    // gameService
    private final GameService gameService;

    /**
     * 게임 상세 정보 조회
     *
     * @param id 게임 ID
     * @return GameDetailResponse 게임 상세 정보
     */
    @Operation(
            summary = "게임 상세 정보 조회",
            description = "게임 ID를 통해 상세 정보를 조회합니다. 태그, 리뷰 정보가 포함됩니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public GameDetailResponse getGameDetail(
            @Parameter(description = "조회할 게임의 ID", required = true, example = "1")
            @PathVariable Long id) {

        return gameService.getGameDetail(id);
    }

    @Operation(
            summary = "추천 게임 목록 조회",
            description = "평점과 리뷰 수를 기준으로 추천 게임 목록을 조회합니다. (기본 5개)"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/recommend")
    public Page<GameCardResponse> getRecommendGame(@PageableDefault(size = 5) Pageable pageable) {

        // 서비스 호출
        return gameService.getRecommendGame(pageable);
    }

    /**
     * 게임 검색 (다양한 조건)
     *
     * @param request 검색 조건 (이름, 태그, 가격, 정렬 등)
     * @param pageable 페이징 정보
     * @return 검색된 게임 목록
     */
    @Operation(
            summary = "게임 검색",
            description = "다양한 조건(이름, 태그, 가격 범위, 정렬)으로 게임을 검색합니다."
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public Page<GameCardResponse> searchGames(
            @Valid @ModelAttribute GameSearchRequest request,
            @RequestParam(required = false) List<GameTagType> tags,
            @PageableDefault(size = 10) Pageable pageable) {

        // tags 파라미터를 request에 수동 설정
        if (tags != null && !tags.isEmpty()) {
            request.setTags(tags);
        }

        return gameService.searchGames(request, pageable);
    }
  
    @Operation(
            summary = "게임 이름 검색",
            description = "게임 이름으로 게임을 검색합니다. 부분 일치 검색을 지원하며, 대소문자를 구분하지 않습니다. (기본 5개)"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/title")
    public Page<GameCardResponse> getSearchByGameTitle(
            @Parameter(description = "검색할 게임 이름", required = true, example = "Minecraft")
            @RequestParam String gameTitle,
            @PageableDefault(size = 5) Pageable pageable) {

        return gameService.getSearchByTitle(gameTitle, pageable);
    }
}

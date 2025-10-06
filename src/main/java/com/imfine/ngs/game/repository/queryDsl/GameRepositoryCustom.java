package com.imfine.ngs.game.repository.queryDsl;

import com.imfine.ngs.game.dto.request.GameSearchRequest;
import com.imfine.ngs.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDsl을 사용한 Game 커스텀 Repository 인터페이스
 *
 * @author chan
 */
public interface GameRepositoryCustom {

    /**
     * 다양한 조건으로 게임 조회
     * @param request 검색 조건 (게임 이름, 태그, 가격 범위, 평점, 정렬 등)
     * @param pageable 페이징 정보
     * @return 검색된 게임 리스트 (페이징 처리)
     */
    Page<Game> searchGames(GameSearchRequest request, Pageable pageable);
}

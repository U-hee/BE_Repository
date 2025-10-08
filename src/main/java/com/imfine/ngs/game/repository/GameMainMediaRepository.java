package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.GameMainMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link GameMainMedia} 리포지토리 인터페이스.
 *
 * @author chan
 */
@Repository
public interface GameMainMediaRepository extends JpaRepository<GameMainMedia, Long> {

    /**
     * 게임 ID로 미디어 목록 조회
     *
     * @param gameId 게임 ID
     * @return 미디어 URL 목록
     */
    List<GameMainMedia> findByGameId(Long gameId);

    /**
     * 게임 ID로 미디어 삭제
     *
     * @param gameId 게임 ID
     */
    void deleteByGameId(Long gameId);
}

package com.imfine.ngs.game.service.search;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임(Game) 검색 서비스 클래스.
 * 현재는 테스트코드를 위해 간소화 된 상태이다.
 *
 * @author chan
 */
@RequiredArgsConstructor
@Service
public class GameSearchService {

    private final GameRepository gameRepository;

    // 게임 등록 로직 (테스트용)
    public void registerGame(Game game) {
        gameRepository.save(game);
    }

    // 게임 전체 조회 로직
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    // 조건별 조회

    // Env 환경으로 조회하기
    public List<Game> findByEnv(String env) {
        return gameRepository.findByEnv(env);
    }

    public List<Game> findByTag(String tag) {
        return gameRepository.findByTag(tag);
    }

    public List<Game> findByPriceBetween(long minPrice, long maxPrice) {
        return gameRepository.findByPriceBetween(minPrice, maxPrice);
    }

}

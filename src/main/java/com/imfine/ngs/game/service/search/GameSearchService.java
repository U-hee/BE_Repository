package com.imfine.ngs.game.service.search;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임({@link Game}) 검색 서비스 클래스.
 * {@link Game}의 isActive가 활성화 되어야 조회가 가능하다.
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

        return gameRepository.findAllActive();
    }

    // 조건별 조회

    // 제목으로 조회
    public List<Game> findByGameName(String gameName) {
        return gameRepository.findActiveByName(gameName);
    }


    // Env환경으로 조회하기
    public List<Game> findByEnv(String env) {

        // 유효성 검사
        if (StringUtils.isEmpty(env)) {
            throw new RuntimeException("env is empty");
        }

        return gameRepository.findActiveByEnvType(env);
    }

    // Tag로 조회하기
    public List<Game> findByTag(String tag) {

        // 유효성 검사
        if (StringUtils.isEmpty(tag)) {
            throw new RuntimeException("tag is empty");
        }
        return gameRepository.findActiveByTag(tag);
    }

    // 가격으로 조회하기
    public List<Game> findByPriceBetween(long minPrice, long maxPrice) {

        // 유효성 검사
        if (minPrice > maxPrice || minPrice == maxPrice) {
            throw new RuntimeException("minPrice or MaxPrice is less than or equal to maxPrice");
        }

        return gameRepository.findActiveByPrice(minPrice, maxPrice);
    }
}

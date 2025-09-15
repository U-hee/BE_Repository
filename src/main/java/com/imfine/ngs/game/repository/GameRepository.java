package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    // evn로 조회
    List<Game> findByEnv(String env);

    // tag로 조회
    List<Game> findByTag(String tag);

    // 가격으로 조회
    List<Game> findByPriceBetween(long minPrice, long maxPrice);
}

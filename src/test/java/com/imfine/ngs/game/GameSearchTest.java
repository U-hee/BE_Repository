package com.imfine.ngs.game;

import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.game.service.search.GameSearchService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 게임 조회 테스트 클래스.
 * 메인페이지에서 게임 조회
 * 조견별 게임 조회 테스트
 *
 * @author chan
 */
@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class GameSearchTest {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameSearchService gameSearchService;

    // 게임을 생성하는 테스트 코드
    @BeforeEach
    void dataInitializer() {

        for (int i = 0; i < 10; i++) {
            Game macGame = Game.builder()
                    .name("TestGame" + i)
                    .price(10000L + i)
                    .env("Mac")
                    .tag("Action")
                    .build();

            gameRepository.save(macGame);
        }

        for (int i = 10; i < 15; i++) {
            Game windGame = Game.builder()
                    .name("TestGame" + i)
                    .price(20000L + i)
                    .env("Window")
                    .tag("RPG")
                    .build();

            gameRepository.save(windGame);
        }
    }

    @DisplayName("게임 전체 조회 테스트")
    @Test
    void mainPageFindAllGame() {

        // Given
        // dataInitializer()

        // When
        // 서비스에서 게임을 가져와야한다.
        List<Game> result = gameRepository.findAll();

        // Then
        // null인가요?
        // 생성한 개수가 일치하는가요?
        assertNotNull(result);
        assertEquals(15, result.size());

        // 첫 번째 게임 검증
        Game firstGame = result.stream()
                .filter(game -> game.getName().equals("TestGame0"))
                .findFirst()
                .orElse(null);

        assertNotNull(firstGame);
        assertEquals("TestGame0", firstGame.getName());
        assertEquals(10000L, firstGame.getPrice());
        assertEquals("Mac", firstGame.getEnv());
        assertEquals("Action", firstGame.getTag());

    }

    // OS별 검색
    @DisplayName("게임 실행환경으로 조회")
    @Test
    void findByEnv() {

        // given
        // Mac 게임 10개 생성
        // Window 게임 5개 생성

        // when
        List<Game> macGames = gameSearchService.findByEnv("Mac");
        List<Game> windowGames = gameSearchService.findByEnv("Window");

        // then
        // null인가요?
        assertNotNull(macGames);
        assertNotNull(windowGames);

        // 저장된 사이즈가 일치하나요?
        assertEquals(10, macGames.size());
        assertEquals(5, windowGames.size());

        // 리스트의 모든 OS환경이 일치하나요?
        assertTrue(macGames.stream().allMatch(game -> "Mac".equals(game.getEnv())));
        assertTrue(windowGames.stream().allMatch(game -> "Window".equals(game.getEnv())));
    }

    // 태그별 검색
    @DisplayName("게임 태그로 검색")
    @Test
    void findByTags() {

        // given
        // Action 게임 10개 생성
        // RPG 게임 5개 생성

        // when
        // 태그 게임들을 불러온다.
        List<Game> actionGame = gameSearchService.findByTag("Action");
        List<Game> rpgGame = gameSearchService.findByTag("RPG");

        // then
        // null인가요?
        assertNotNull(actionGame);
        assertNotNull(rpgGame);

        // 사이즈가 일치하나요?
        assertEquals(10, actionGame.size());
        assertEquals(5, rpgGame.size());

        // 리스트의 모든 게임이 태그와 일치하나요?
        assertTrue(actionGame.stream().allMatch(game -> "Action".equals(game.getTag())));
        assertTrue(rpgGame.stream().allMatch(game -> "RPG".equals(game.getTag())));
    }

    // 가격별 검색
    @DisplayName("가격별 검색 테스트")
    @Test
    void findByPrice() {

        // given
        // beforeEach에서 설정하였다.

        // when
        // Mac과 Window 리스트 객체를 추가한다.
        List<Game> cheapGames = gameSearchService.findByPriceBetween(10000L, 10005L);
        List<Game> expensiveGames = gameSearchService.findByPriceBetween(20010L, 20014L);

        // then
        // null인가요?
        assertNotNull(cheapGames);
        assertNotNull(expensiveGames);

        // 싼 상품의 개수가 6개인가요?
        assertEquals(6, cheapGames.size());
        // 비싼 제품의 개수가 5개인가요?
        assertEquals(5, expensiveGames.size());

        // 싼 상품의 가격은 일치하는가요?
        assertTrue(cheapGames.stream().allMatch(game -> game.getPrice() >= 10000L && game.getPrice() <= 10005L));

        // 비싼 상품의 가격은 일치하는가요?
        assertTrue(expensiveGames.stream().allMatch(game -> game.getPrice() >= 20010L && game.getPrice() <= 20014L));
    }

}

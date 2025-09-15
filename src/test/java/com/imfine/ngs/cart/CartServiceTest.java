package com.imfine.ngs.cart;

import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;
import com.imfine.ngs.cart.service.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartServiceTest {
    Cart cart = new Cart();
    CartService cartService = new CartService();

    @BeforeEach
    void setUp() {
        cartService = new CartService();
        cart = new Cart();
    }

    @Test
    @DisplayName("비어있는 장바구니에 게임을 추가하면 아이템 개수는 1이 된다.")
    void addGameToEmptyCart() {
        //given
        Game newGame = new Game("Overcooked", 22000);

        //when
        cartService.addGame(cart, newGame);

        //then
        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("아이템이 있는 장바구니에 게임을 삭제하면 아이템 개수는 0이 된다.")
    void removeGameFromCart() {
        //given
        Game gameToRemove = new Game("ItTakesTwo", 25000);

        //테스트를 위해 장바구니에 아이템을 추가
        cartService.addGame(cart, gameToRemove);

        //when
        cartService.removeGame(cart, gameToRemove);

        //then
        assertThat(cart.getItemCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("한 장바구니에는 같은 게임을 중복해서 담을 수 없다.")
    void addDuplicateGameToCart() {

        //given
        Game newGame = new Game("Among us", 5500);

        //when
        cartService.addGame(cart, newGame);

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> cartService.addGame(cart, newGame));

        assertThat(cart.getItemCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("장바구니에 여러 개의 다른 게임을 추가하면, 추가한 개수만큼 아이템이 늘어난다.")
    void addMultipleGamesToCart() {
        //given
        Game game1 = new Game("PICO PARK", 5500);
        Game game2 = new Game("Split Fiction", 65000);

        //when
        cartService.addGame(cart, game1);
        cartService.addGame(cart,game2);

        //then
        assertThat(cart.getItemCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 삭제된 게임은 장바구니에서 삭제할 수 없다.")
    void removeNonExistentGameFromCart() {
        //given
        Game nonExistentGame = new Game("Stardew Valley", 16500);

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> cartService.removeGame(cart, nonExistentGame));
    }
}

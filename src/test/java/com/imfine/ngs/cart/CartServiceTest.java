package com.imfine.ngs.cart;

import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;
import com.imfine.ngs.cart.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CartServiceTest {

    @Test
    @DisplayName("비어있는 장바구니에 게임을 추가하면 아이템 개수는 1이 된다.")
    void addGameToEmptyCart() {
        //given
        Cart cart = new Cart();
        CartService cartService = new CartService();
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
        Cart cart = new Cart();
        CartService cartService = new CartService();
        Game gameToRemove = new Game("ItTakesTwo", 25000);

        //테스트를 위해 장바구니에 아이템을 추가
        cartService.addGame(cart, gameToRemove);

        //when
        cartService.removeGame(cart, gameToRemove);

        //then
        assertThat(cart.getItemCount()).isEqualTo(0);
    }
}

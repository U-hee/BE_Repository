package com.imfine.ngs.order;

import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;
import com.imfine.ngs.cart.service.CartService;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderServiceTest {

    OrderService orderService = new OrderService();
    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    @DisplayName("아이템이 담긴 장바구니를 기반으로 주문을 생성할 수 있다.")
    void createOrderFromCart() {
        //given
        Game game1 = new Game("It Takes Two", 25000);
        Game game2 = new Game("Split Fiction", 54000);

        Cart cart = new Cart();

        CartService cartService = new CartService();
        cartService.addGame(cart, game1);
        cartService.addGame(cart, game2);

        //when
        Order newOrder = orderService.createOrder(cart);

        //then
        assertThat(newOrder).isNotNull();
        assertThat(newOrder.getTotalPrice()).isEqualTo(79000);
        assertThat(newOrder.getOrderItemCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 장바구니로는 주문을 생성할 수 없다.")
    void createOrderWithEmptyCart() {
        //given
        Cart emptyCart = new Cart();

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(emptyCart));
    }
}

package com.imfine.ngs.order;

import com.imfine.ngs.order.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    // 매 테스트 시작 시 주문 목록 초기화
    void setUp() {
        orderService.clearOrders();
    }

    @Test
    @DisplayName("사용자가 고른 게임을 기반으로 주문을 생성할 수 있다.")
    void createOrderFromGameList() {
        //given
        Game game1 = new Game("It Takes Two", 25000);
        Game game2 = new Game("Split Fiction", 54000);
        List<Game> games = Arrays.asList(game1, game2);

        //when
        Order newOrder = orderService.createOrder(games);

        //then
        assertThat(newOrder).isNotNull();
        assertThat(newOrder.getTotalPrice()).isEqualTo(79000);
        assertThat(newOrder.getOrderItemCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 게임 목록으로는 주문을 생성할 수 없다.")
    void createOrderWithEmptyGameList() {
        //given
        List<Game> emptyGames = new ArrayList<>();

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(emptyGames));
    }

    @Test
    @DisplayName("주문 목록을 정상적으로 반환한다.")
    void getOrderListReturnsOrders() {
        //given
        Game game1 = new Game("Game A", 10000);
        Game game2 = new Game("Game B", 20000);
        List<Game> games1 = Arrays.asList(game1);
        Order order1 = orderService.createOrder(games1);

        List<Game> games2 = Arrays.asList(game2);
        Order order2 = orderService.createOrder(games2);

        //when
        List<Order> orders = orderService.getOrderList();

        //then
        assertThat(orders).isNotNull();
        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders).contains(order1, order2);
    }

    @Test
    @DisplayName("비어있는 주문에 게임을 추가하면 아이템 개수는 1이 된다.")
    void addGameToEmptyOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game newGame = new Game("Overcooked", 22000);

        //when
        orderService.addGameToOrder(order, newGame);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(22000);
    }

    @Test
    @DisplayName("아이템이 있는 주문에 게임을 삭제하면 아이템 개수는 0이 된다.")
    void removeGameFromOrder() {
        //given
        Order order = new Order();
        Game gameToRemove = new Game("ItTakesTwo", 25000);
        order.setOrderItems(new ArrayList<>(Arrays.asList(gameToRemove)));
        order.setTotalPrice(25000);

        //when
        orderService.removeGameFromOrder(order, gameToRemove);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(0);
        assertThat(order.getTotalPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("한 주문에는 같은 게임을 중복해서 담을 수 없다.")
    void addDuplicateGameToOrder() {
        //given
        Order order = new Order();
        Game newGame = new Game("Among us", 5500);
        order.setOrderItems(new ArrayList<>(Arrays.asList(newGame)));
        order.setTotalPrice(5500);

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.addGameToOrder(order, newGame));
        assertThat(order.getOrderItemCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문에 여러 개의 다른 게임을 추가하면, 추가한 개수만큼 아이템이 늘어난다.")
    void addMultipleGamesToOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game game1 = new Game("PICO PARK", 5500);
        Game game2 = new Game("Split Fiction", 65000);

        //when
        orderService.addGameToOrder(order, game1);
        orderService.addGameToOrder(order, game2);

        //then
        assertThat(order.getOrderItemCount()).isEqualTo(2);
        assertThat(order.getTotalPrice()).isEqualTo(70500);
    }

    @Test
    @DisplayName("이미 삭제된 게임은 주문에서 삭제할 수 없다.")
    void removeNonExistentGameFromOrder() {
        //given
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setTotalPrice(0);
        Game nonExistentGame = new Game("Stardew Valley", 16500);

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.removeGameFromOrder(order, nonExistentGame));
    }
}
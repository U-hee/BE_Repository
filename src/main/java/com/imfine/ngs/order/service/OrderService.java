package com.imfine.ngs.order.service;

import com.imfine.ngs.order.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final List<Order> orders = new ArrayList<>();

    public Order createOrder(List<Game> games) {
        if (games.isEmpty()) {
            throw new IllegalArgumentException("주문할 게임이 없습니다.");
        }

        Order order = new Order();
        order.setOrderItems(new ArrayList<>(games)); // 원본 리스트 보호
        order.setTotalPrice(calculateTotalPrice(games));
        order.setOrderStatus(OrderStatus.PENDING);

        orders.add(order);
        return order;
    }

    private long calculateTotalPrice(List<Game> games) {
        return games.stream()
                .mapToLong(Game::getPrice)
                .sum();
    }

    public void addGameToOrder(Order order, Game game) {
        if (order.getOrderItems().contains(game)) {
            throw new IllegalArgumentException("이미 주문에 담긴 게임입니다.");
        }
        order.getOrderItems().add(game);
        order.setTotalPrice(order.getTotalPrice() + game.getPrice());
    }

    public void removeGameFromOrder(Order order, Game game) {
        if (!order.getOrderItems().contains(game)) {
            throw new IllegalArgumentException("주문에 없는 게임입니다.");
        }
        order.getOrderItems().remove(game);
        order.setTotalPrice(order.getTotalPrice() - game.getPrice());
    }

    public Order findByOrderId(long orderId) {
        // TODO: 추후 DB 연동 시 구현
        return orders.stream()
                .filter(order -> order.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
    }

    public void updateOrderStatus(long orderId, OrderStatus status) {
        Order order = findByOrderId(orderId);
        if (order != null) {
            order.setOrderStatus(status);
        }
    }

    public List<Order> getOrderList() {
        return new ArrayList<>(orders);
    }

    // 테스트에서 사용하기 위한 헬퍼 메서드
    public void clearOrders() {
        orders.clear();
    }
}

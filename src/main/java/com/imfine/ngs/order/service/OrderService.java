package com.imfine.ngs.order.service;

import com.imfine.ngs.order.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final Map<String, List<Order>> userOrders = new HashMap<>();
    private final AtomicLong orderIdCounter = new AtomicLong();

    public Order createOrder(String userId, List<Game> games) {
        if (games.isEmpty()) {
            throw new IllegalArgumentException("주문할 게임이 없습니다.");
        }

        Order order = new Order();
        order.setOrderId(orderIdCounter.incrementAndGet());
        order.setUserId(userId);
        order.setOrderItems(new ArrayList<>(games));
        order.setTotalPrice(calculateTotalPrice(games));
        order.setOrderStatus(OrderStatus.PENDING);

        userOrders.computeIfAbsent(userId, k -> new ArrayList<>()).add(order);

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
        return userOrders.values().stream()
                .flatMap(List::stream)
                .filter(order -> order.getOrderId() == orderId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));
    }

    public void updateOrderStatus(long orderId, OrderStatus status) {
        Order order = findByOrderId(orderId);
        if (order != null) {
            order.setOrderStatus(status);
        }
    }

    public List<Order> getOrdersByUserId(String userId) {
        return userOrders.getOrDefault(userId, new ArrayList<>());
    }

    public List<Order> getAllOrders() {
        return userOrders.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // 테스트에서 사용하기 위한 헬퍼 메서드
    public void clearAllOrders() {
        userOrders.clear();
        orderIdCounter.set(0);
    }
}

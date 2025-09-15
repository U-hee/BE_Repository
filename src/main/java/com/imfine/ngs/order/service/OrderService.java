package com.imfine.ngs.order.service;

import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public Order createOrder(Cart cart) {
        Order order = new Order();
        if(cart.getItems().isEmpty()){
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }
        order.setOrderItems(cart);
        order.setTotalPrice(calculateTotalPrice(cart));
        return order;
    }

    private long calculateTotalPrice(Cart cart) {
        long totalPrice = 0;
        for (Game game : cart.getItems()) {
            totalPrice += game.getPrice();
        }

        return totalPrice;
    }

    public Order findByOrderId(long orderId) {
        // TODO: 테스트를 위한 임시 구현
        return null;
    }

    public void updateOrderStatus(long orderId, OrderStatus status) {
        // TODO: 테스트를 위한 임시 구현
        System.out.println("Order " + orderId + " status updated to " + status);
    }
}

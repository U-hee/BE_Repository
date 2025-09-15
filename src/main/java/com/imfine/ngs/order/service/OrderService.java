package com.imfine.ngs.order.service;

import com.imfine.ngs.cart.entity.Cart;
import com.imfine.ngs.cart.entity.Game;
import com.imfine.ngs.order.entity.Order;

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
}

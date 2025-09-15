package com.imfine.ngs.order.entity;

import com.imfine.ngs.cart.entity.Cart;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private long orderId;
    private String userId;
    private Cart orderItems;
    private long totalPrice;
    private OrderStatus orderStatus;

    public Order(long orderId, long totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public int getOrderItemCount() {
        if (orderItems == null || orderItems.getItems() == null) {
            return 0;
        }
        return orderItems.getItems().size();
    }
}

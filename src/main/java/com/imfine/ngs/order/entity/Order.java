package com.imfine.ngs.order.entity;

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
    private List<Game> orderItems;
    private long totalPrice;
    private OrderStatus orderStatus;

    public Order(long orderId, long totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public int getOrderItemCount() {
        if (orderItems == null) {
            return 0;
        }
        return orderItems.size();
    }
}

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

    private String userId;
    private Cart orderItems;
    private long totalPrice;
    private OrderStatus orderStatus;
}

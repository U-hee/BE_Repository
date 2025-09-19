package com.imfine.ngs.order.entity;

import com.imfine.ngs.game.entity.Game;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    private long userId;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> orderItems = new ArrayList<>();

    private long totalPrice;

    @Enumerated(EnumType.STRING)
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

    // PaymentService에서 가정한 메소드들 추가
    public boolean isPaid() {
        return this.orderStatus == OrderStatus.PAYMENT_COMPLETED;
    }

    public long getTotalAmount() {
        return this.totalPrice;
    }

    public void paymentCompleted() {
        this.orderStatus = OrderStatus.PAYMENT_COMPLETED;
    }

    public void paymentFailed() {
        this.orderStatus = OrderStatus.PAYMENT_FAILED;
    }

    // merchantUid 필드 (PortOne의 merchant_uid와 매핑)
    private String merchantUid;
}

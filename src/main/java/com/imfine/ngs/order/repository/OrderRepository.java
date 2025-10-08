package com.imfine.ngs.order.repository;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatusNot(Long userId, OrderStatus status);

    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // 장바구니 조회 (merchantUid가 null이고 PENDING 상태)
    Optional<Order> findByUserIdAndStatusAndMerchantUidIsNull(Long userId, OrderStatus status);

    Optional<Order> findByMerchantUid(String merchantUid);

}

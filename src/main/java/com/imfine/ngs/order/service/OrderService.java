package com.imfine.ngs.order.service;

import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.game.entity.Game;
import com.imfine.ngs.game.repository.GameRepository;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderDetails;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderDetailsRepository;
import com.imfine.ngs.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final GameRepository gameRepository;

    // 장바구니(PENDING 상태의 주문) 조회 또는 생성
    public Order getOrCreateCart(Long userId) {
        // merchantUid가 null이고 PENDING 상태인 것만 장바구니로 간주
        return orderRepository.findByUserIdAndStatusAndMerchantUidIsNull(userId, OrderStatus.PENDING)
                .orElseGet(() -> orderRepository.save(new Order(userId)));
    }

    // 장바구니에 게임 추가
    public Order addGameToCart(Long userId, Long gameId) {
        Order cart = getOrCreateCart(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // 이미 장바구니에 담겼는지 확인
        boolean isAlreadyInCart = cart.getOrderDetails().stream()
                .anyMatch(detail -> detail.getGame().equals(game));

        if (isAlreadyInCart) {
            throw new BusinessException(ErrorCode.GAME_ALREADY_IN_CART);
        }

        OrderDetails orderDetails = new OrderDetails(cart, game);
        cart.addOrderDetail(orderDetails);

        return orderRepository.save(cart);
    }

    // 장바구니에서 게임 삭제
    public Order removeGameFromCart(Long userId, Long gameId) {
        Order cart = getOrCreateCart(userId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        OrderDetails detailToRemove = cart.getOrderDetails().stream()
                .filter(detail -> detail.getGame().equals(game))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_IN_CART));

        cart.getOrderDetails().remove(detailToRemove);
        orderDetailsRepository.delete(detailToRemove);

        return orderRepository.save(cart);
    }

    // 장바구니 전체 비우기
    public Order clearCart(Long userId) {
        Order cart = getOrCreateCart(userId);

        // 모든 OrderDetails 삭제
        List<OrderDetails> detailsToRemove = List.copyOf(cart.getOrderDetails());
        cart.getOrderDetails().clear();
        orderDetailsRepository.deleteAll(detailsToRemove);

        return orderRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        // PENDING 상태 주문 제외 (장바구니와 결제 전 주문 제외)
        return orderRepository.findByUserIdAndStatusNot(userId, OrderStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public Order getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 본인의 주문인지 확인
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        return order;
    }

    // 장바구니에서 주문 생성 (결제 전)
    public Order createOrderFromCart(Long userId) {
        Order cart = getOrCreateCart(userId);

        // 장바구니가 비어있는지 확인
        if (cart.getOrderDetails().isEmpty()) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
        }

        // 새로운 주문 생성 (merchantUid 생성)
        Order newOrder = new Order(userId, true);

        // 장바구니 아이템들을 새 주문으로 복사
        for (OrderDetails cartDetail : cart.getOrderDetails()) {
            OrderDetails orderDetail = new OrderDetails(newOrder, cartDetail.getGame());
            newOrder.addOrderDetail(orderDetail);
        }

        // 장바구니 비우기 (순서 중요!)
        List<OrderDetails> detailsToRemove = List.copyOf(cart.getOrderDetails());
        cart.getOrderDetails().clear();
        orderDetailsRepository.deleteAll(detailsToRemove);

        return orderRepository.save(newOrder);
    }

    // 주문 취소 (PAYMENT_COMPLETED 상태만 가능)
    public Order cancelOrder(Long userId, Long orderId) {
        Order order = getOrderDetail(userId, orderId);

        // 결제 완료 상태가 아니면 취소 불가
        if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 주문 상태를 환불 요청으로 변경
        order.setStatus(OrderStatus.REFUND_REQUESTED);

        return orderRepository.save(order);
    }
}
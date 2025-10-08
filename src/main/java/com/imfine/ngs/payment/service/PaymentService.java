package com.imfine.ngs.payment.service;

import com.imfine.ngs._global.error.exception.BusinessException;
import com.imfine.ngs._global.error.model.ErrorCode;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderHistory;
import com.imfine.ngs.order.repository.OrderHistoryRepository;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.entity.Payment;
import com.imfine.ngs.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final OrderRepository orderRepository;
    private final PortOneApiClient portOneApiClient;
    private final PaymentRepository paymentRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Transactional
    public PaymentCompleteResponse completePayment(String paymentId, String merchantUid, Long amount) {
        logger.info("결제 완료 처리 시작 - paymentId: {}, merchantUid: {}, amount: {}", paymentId, merchantUid, amount);

        // 1. 우리 DB에서 주문 정보 조회
        final Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        try {
            // 2. 이미 처리된 주문인지 확인 (우리 DB)
            if (order.isPaid()) {
                throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
            }

            // 3. 결제 금액 검증
            final long expectedAmount = order.getTotalPrice();
            logger.debug("금액 검증 - 결제금액: {}, 주문금액: {}", amount, expectedAmount);

            if (amount == null || amount != expectedAmount) {
                logger.error("금액 불일치 - 결제금액: {}, 주문금액: {}", amount, expectedAmount);
                throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
            }

            // 4. 모든 검증 통과 -> 주문 상태를 '결제 완료'로 변경
            order.paymentCompleted();
            orderHistoryRepository.save(new OrderHistory(order, order.getStatus()));

            // 5. Payment 객체 생성 및 저장
            Payment payment = new Payment(order, amount, paymentId);
            paymentRepository.save(payment);

            logger.info("결제 완료 처리 성공 - orderId: {}, paymentId: {}", order.getOrderId(), paymentId);
            return new PaymentCompleteResponse("PAID", "결제가 성공적으로 완료되었습니다.");

        } catch (BusinessException e) {
            // 검증 과정에서 비즈니스 예외 발생 시 주문 상태를 '결제 실패'로 변경
            logger.error("결제 검증 실패 - orderId: {}, error: {}", order.getOrderId(), e.getMessage());
            order.paymentFailed();
            orderHistoryRepository.save(new OrderHistory(order, order.getStatus()));
            throw e;
        }
    }
}

package com.imfine.ngs.payment.service;

import com.imfine.ngs.order.entity.Order;
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
    private final PaymentRepository paymentRepository; // PaymentRepository 주입

    @Transactional
    public PaymentCompleteResponse completePayment(String paymentId) {
        try {
            // 1. PortOne API를 통해 결제 정보 조회
            PortOnePaymentData portOnePayment = portOneApiClient.getPayment(paymentId);

            if (portOnePayment == null) {
                throw new RuntimeException("PortOne API로부터 결제 정보를 받지 못했습니다.");
            }
            logger.info("PortOne 결제 정보 조회 성공: {}", portOnePayment);

            // 2. 결제 상태 확인
            if (!"PAID".equalsIgnoreCase(portOnePayment.getStatus())) {
                logger.warn("결제가 완료되지 않은 상태입니다. (paymentId: {}, 상태: {})", paymentId, portOnePayment.getStatus());
                throw new RuntimeException("결제가 완료되지 않았습니다. 상태: " + portOnePayment.getStatus());
            }

            // 3. 우리 DB에서 주문 정보 조회 (PortOne의 merchantUid는 우리 시스템의 주문 ID)
            final Order order = orderRepository.findByMerchantUid(portOnePayment.getMerchantUid())
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 주문을 찾을 수 없습니다. merchantUid=" + portOnePayment.getMerchantUid()));

            // 4. 이미 처리된 주문인지 확인
            if (order.isPaid()) { // Order 엔티티에 isPaid()와 같은 상태 확인 메소드가 있다고 가정합니다.
                logger.warn("이미 결제 처리된 주문입니다. (orderId: {})", order.getOrderId());
                return new PaymentCompleteResponse("PAID", "이미 처리된 주문입니다.");
            }

            // 5. 결제 금액 검증
            final long paidAmount = portOnePayment.getAmount().getTotal();
            final long expectedAmount = order.getTotalAmount(); // Order 엔티티에 getTotalAmount()가 있다고 가정

            if (paidAmount != expectedAmount) {
                order.paymentFailed(); // 주문 상태를 '결제 실패'로 변경
                portOneApiClient.cancelPayment(paymentId); // PortOne API를 통한 결제 취소 호출
                throw new RuntimeException("결제 금액(" + paidAmount + ")이 주문 금액(" + expectedAmount + ")과 일치하지 않습니다.");
            }

            // 6. 모든 검증 통과 -> 주문 상태를 '결제 완료'로 변경
            order.paymentCompleted();

            // 7. Payment 객체 생성 및 저장
            Payment payment = new Payment(order, paidAmount, paymentId);
            paymentRepository.save(payment);

            return new PaymentCompleteResponse("PAID", "결제가 성공적으로 완료되었습니다.");

        } catch (Exception e) {
            logger.error("결제 검증 처리 중 심각한 오류 발생 (paymentId: {})", paymentId, e);
            throw new RuntimeException("결제 검증 중 오류 발생: " + e.getMessage(), e);
        } 
    }
}
package com.imfine.ngs.payment;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.client.PortOneClient;
import com.imfine.ngs.payment.dto.PaymentRequest;
import com.imfine.ngs.payment.dto.PaymentResponse;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private PortOneClient portOneClient;

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 일치하면, 주문 상태를 COMPLETED로 변경한다.")
    void testPaymentSuccess() {
        //given
        long orderId = 12345L;
        String impUid = "imp-456";
        long correctAmount = 50000L;

        given(orderService.findByOrderId(orderId))
                .willReturn(new Order(orderId, correctAmount));
        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, correctAmount));

        //when
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        paymentService.verifyPayment(request);

        //then
        verify(orderService).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 다르면, 예외가 발생하고 결제 취소를 요청한다")
    void testPaymentFailed() {
        // given
        long orderId = 12345L;
        String impUid = "imp-456";
        long orderAmount = 50000L; // 주문 금액
        long paidAmount = 40000L;  // 실제 결제된 금액 (위변조)

        given(orderService.findByOrderId(orderId))
                .willReturn(new Order(orderId, orderAmount));
        given(portOneClient.getPaymentData(impUid))
                .willReturn(new PaymentResponse(impUid, paidAmount));

        // when & then
        PaymentRequest request = new PaymentRequest(orderId, impUid);
        assertThrows(RuntimeException.class, () -> {
            paymentService.verifyPayment(request);
        });

        verify(portOneClient).cancelPayment(impUid, "결제 금액 위변조 의심");
        verify(orderService).updateOrderStatus(orderId, com.imfine.ngs.order.entity.OrderStatus.PAYMENT_FAILED);
    }

}
package com.imfine.ngs.payment;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.entity.OrderStatus;
import com.imfine.ngs.order.repository.OrderRepository;
import com.imfine.ngs.payment.client.PortOneApiClient;
import com.imfine.ngs.payment.client.PortOneAmount;
import com.imfine.ngs.payment.client.PortOnePaymentData;
import com.imfine.ngs.payment.dto.PaymentCompleteResponse;
import com.imfine.ngs.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PortOneApiClient portOneApiClient;

    private Order testOrder;
    private String testPaymentId;
    private long testAmount;

    @BeforeEach
    void setUp() {
        testPaymentId = "PAY-test-123";
        testAmount = 50000L;
        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setMerchantUid(testPaymentId);
        testOrder.setTotalPrice(testAmount);
        testOrder.setOrderStatus(OrderStatus.PENDING);
    }

    private PortOnePaymentData createPortOnePaymentData(String paymentId, long amount, String status) {
        PortOneAmount amountObject = new PortOneAmount(amount, 0, 0, amount, 0, amount, 0, 0);
        return new PortOnePaymentData(paymentId, paymentId, status, amountObject, "테스트 상품", "KRW", null, null, null, null, null);
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 일치하면, 주문 상태를 COMPLETED로 변경한다.")
    void testPaymentSuccess() {
        //given
        given(orderRepository.findByMerchantUid(testPaymentId))
                .willReturn(Optional.of(testOrder));
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PAID"));

        //when
        paymentService.completePayment(testPaymentId);

        //then
        assertThat(testOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
        verify(orderRepository).findByMerchantUid(testPaymentId);
        verify(portOneApiClient).getPayment(testPaymentId);
    }

    @Test
    @DisplayName("실제 결제 금액과 주문 금액이 다르면, 예외가 발생하고 주문 상태를 FAILED로 변경한다")
    void testPaymentAmountMismatch() {
        // given
        long paidAmount = 40000L;  // 실제 결제된 금액 (위변조)

        given(orderRepository.findByMerchantUid(testPaymentId))
                .willReturn(Optional.of(testOrder));
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, paidAmount, "PAID"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.completePayment(testPaymentId);
        });

        assertThat(exception.getMessage()).contains("결제 금액(" + paidAmount + ")이 주문 금액(" + testAmount + ")과 일치하지 않습니다.");
        assertThat(testOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        verify(orderRepository).findByMerchantUid(testPaymentId);
        verify(portOneApiClient).getPayment(testPaymentId);
    }

    @Test
    @DisplayName("이미 결제 완료된 주문을 다시 결제 시도하면 성공 응답을 반환한다.")
    void testPaymentAlreadyCompleted() {
        //given
        testOrder.setOrderStatus(OrderStatus.PAYMENT_COMPLETED);

        given(orderRepository.findByMerchantUid(testPaymentId))
                .willReturn(Optional.of(testOrder));
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PAID"));

        //when
        PaymentCompleteResponse response = paymentService.completePayment(testPaymentId);

        //then
        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getMessage()).contains("이미 처리된 주문입니다.");
        assertThat(testOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 결제 시도하면 예외가 발생한다")
    void testPaymentOrderNotFound() {
        // given
        given(orderRepository.findByMerchantUid(anyString()))
                .willReturn(Optional.empty());
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PAID"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(testPaymentId));

        assertThat(exception.getMessage()).contains("일치하는 주문을 찾을 수 없습니다");
        verify(orderRepository).findByMerchantUid(testPaymentId);
        verify(portOneApiClient).getPayment(testPaymentId);
    }

    @Test
    @DisplayName("PortOne API 결제 정보 조회 실패 시 예외가 발생한다")
    void testPortOneApiFailure() {
        // given
        given(portOneApiClient.getPayment(testPaymentId))
                .willThrow(new RuntimeException("API 통신 오류"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(testPaymentId));

        assertThat(exception.getMessage()).contains("API 통신 오류");
        verify(portOneApiClient).getPayment(testPaymentId);
        verify(orderRepository, never()).findByMerchantUid(anyString());
    }

    @Test
    @DisplayName("PortOne 결제 상태가 PAID가 아니면 예외가 발생한다")
    void testPortOneStatusNotPaid() {
        // given
        given(portOneApiClient.getPayment(testPaymentId))
                .willReturn(createPortOnePaymentData(testPaymentId, testAmount, "PENDING"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.completePayment(testPaymentId));

        assertThat(exception.getMessage()).contains("결제가 완료되지 않았습니다. 상태: PENDING");
        verify(portOneApiClient).getPayment(testPaymentId);
        verify(orderRepository, never()).findByMerchantUid(anyString());
    }
}

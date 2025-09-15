package com.imfine.ngs.payment.service;

import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.payment.dto.PaymentResponse;
import com.imfine.ngs.payment.client.PortOneClient;
import com.imfine.ngs.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final PortOneClient portOneClient;

    public void verifyPayment(PaymentRequest request) {
        PaymentResponse paymentResponse = portOneClient.getPaymentData(request.getImpUid());
        long paidAmount = paymentResponse.getAmount();

        Order order = orderService.findByOrderId(request.getOrderId());
        long expectedAmount = order.getTotalPrice();

        if (paidAmount == expectedAmount) {
            orderService.updateOrderStatus(request.getOrderId(), com.imfine.ngs.order.entity.OrderStatus.COMPLETED);
        } else {
            orderService.updateOrderStatus(request.getOrderId(), com.imfine.ngs.order.entity.OrderStatus.FAILED);
            portOneClient.cancelPayment(paymentResponse.getImpUid(), "결제 금액 위변조 의심");
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }
    }
}

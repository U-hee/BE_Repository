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

    public void verifyPayment(PaymentRequest requestDto) {
        PaymentResponse paymentResponse = portOneClient.getPaymentData(requestDto.getImpUid());
        long paidAmount = paymentResponse.getAmount();

        Order order = orderService.findByOrderId(requestDto.getOrderId());
        long expectedAmount = order.getTotalPrice();

        if (paidAmount == expectedAmount) {
            orderService.setOrderAsPaid(requestDto.getOrderId());
        } else {
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }
    }
}

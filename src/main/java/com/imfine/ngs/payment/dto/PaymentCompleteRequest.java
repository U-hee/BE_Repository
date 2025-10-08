package com.imfine.ngs.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequest {
    private String paymentId;      // imp_uid
    private String merchantUid;    // 주문번호
    private Long amount;           // 결제 금액
}

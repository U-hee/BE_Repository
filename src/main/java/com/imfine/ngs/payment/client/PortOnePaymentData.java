package com.imfine.ngs.payment.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortOnePaymentData {
    private String id; // PortOne 결제 고유 ID
    @JsonProperty("merchant_uid")
    private String merchantUid; // 가맹점 주문 번호
    private String status; // 결제 상태 (READY, PAID, FAILED, CANCELLED)
    private PortOneAmount amount; // 결제 금액 정보
    @JsonProperty("order_name")
    private String orderName; // 주문명
    private String currency; // 통화 (KRW)
    @JsonProperty("paid_at")
    private Instant paidAt; // 결제 완료 시각
    @JsonProperty("failed_at")
    private Instant failedAt; // 결제 실패 시각
    @JsonProperty("cancelled_at")
    private Instant cancelledAt; // 결제 취소 시각
    @JsonProperty("fail_reason")
    private String failReason; // 결제 실패 사유
    @JsonProperty("cancel_reason")
    private String cancelReason; // 결제 취소 사유
}

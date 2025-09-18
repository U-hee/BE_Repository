package com.imfine.ngs.payment.client;

import com.imfine.ngs._global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "portoneApiClient", url = "https://api.portone.io", configuration = FeignConfig.class)
public interface PortOneApiClient {

    @GetMapping("/payments/{paymentId}")
    PortOnePaymentData getPayment(@PathVariable("paymentId") String paymentId);
}
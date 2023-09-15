package com.paylinkfusion.gateway.client;


import com.paylinkfusion.gateway.client.PaymentClient.FallbackForPaymentService;
import com.paylinkfusion.gateway.models.dto.request.PaymentRequest;
import com.paylinkfusion.gateway.models.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "payment-service", fallback = FallbackForPaymentService.class)
public interface PaymentClient {

    @PostMapping("/payment")
    ResponseEntity<PaymentResponse> handlePaymentRequest(@RequestBody PaymentRequest paymentRequest);

    @Component
    class FallbackForPaymentService implements PaymentClient {
        @Override
        public ResponseEntity<PaymentResponse> handlePaymentRequest(PaymentRequest paymentRequest) {
            return ResponseEntity.ok(
                    new PaymentResponse("Our payment service is currently unavailable. Please try again later."));
        }
    }

}

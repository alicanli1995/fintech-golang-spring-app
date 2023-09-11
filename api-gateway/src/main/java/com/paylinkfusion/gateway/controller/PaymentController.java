package com.paylinkfusion.gateway.controller;


import com.paylinkfusion.gateway.client.PaymentClient;
import com.paylinkfusion.gateway.models.dto.request.PaymentRequest;
import com.paylinkfusion.gateway.models.dto.response.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {


    private final PaymentClient paymentClient;

    @PostMapping
    @CircuitBreaker(name = "beCommon")
    @RateLimiter(name = "beCommon")
    @Retry(name = "beCommon")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest paymentRequest) {
        final var paymentResp = paymentClient.handlePaymentRequest(paymentRequest);
        return ResponseEntity.ok(paymentResp.getBody());
    }


}

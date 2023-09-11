package com.paylinkfusion.payment.controller;


import com.paylinkfusion.payment.models.dto.request.PaymentRequest;
import com.paylinkfusion.payment.models.dto.response.PaymentResponse;
import com.paylinkfusion.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentRequestController {

    private final PaymentService paymentService;
    @PostMapping
    public ResponseEntity<PaymentResponse> handlePaymentRequest(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.handlePaymentRequest(paymentRequest));
    }

}

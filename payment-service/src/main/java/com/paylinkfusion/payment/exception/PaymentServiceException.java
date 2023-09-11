package com.paylinkfusion.payment.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class PaymentServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer errorCode;
    private final String errorMessage;

    public PaymentServiceException(String errorMessage, Integer errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


}

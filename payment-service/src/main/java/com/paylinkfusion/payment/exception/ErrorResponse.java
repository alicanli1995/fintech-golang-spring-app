package com.paylinkfusion.payment.exception;

import lombok.Builder;

@Builder
public record ErrorResponse (
        String errorCode,
        String errorMessage
){
}

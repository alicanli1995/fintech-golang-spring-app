package com.paylinkfusion.gateway.exception;

import lombok.Builder;

@Builder
public record ErrorResponse (
        String errorCode,
        String errorMessage
){
}

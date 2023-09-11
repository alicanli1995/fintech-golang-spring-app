package com.paylinkfusion.gateway.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class AuthServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer errorCode;
    private final String errorMessage;

    public AuthServiceException(String errorMessage, Integer errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


}

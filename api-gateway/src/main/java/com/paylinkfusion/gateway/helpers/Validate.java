package com.paylinkfusion.gateway.helpers;

import com.paylinkfusion.gateway.exception.AuthServiceException;

public class Validate {

    private Validate() {
    }


    /**
     * expression should be TRUE, otherwise throws {@link AuthServiceException}
     */
    public static void state(boolean trueExpectedExpression, Integer errorCode, String errorMsg)
            throws AuthServiceException {
        if (!trueExpectedExpression) {
            throw new AuthServiceException(errorMsg, errorCode);
        }
    }

    /**
     * expression should be FALSE, otherwise throws {@link AuthServiceException}
     */
    public static void stateNot(boolean falseExpectedExpression, Integer errorCode, String errorMsg)
            throws AuthServiceException {
        if (falseExpectedExpression) {
            throw new AuthServiceException(errorMsg, errorCode);
        }
    }


}

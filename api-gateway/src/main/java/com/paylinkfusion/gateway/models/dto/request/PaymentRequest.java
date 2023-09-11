package com.paylinkfusion.gateway.models.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public final class PaymentRequest {

    private BigDecimal amount;
    private String currency;
    private String cardNo;
    private int cardExpiryMonth;
    private int cardExpiryYear;
    private String cardHolderName;
    private String cardCvv;
    private long accountId;
    private String mail;
}

package com.paylinkfusion.payment.models.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AmountDTO(String currency, BigDecimal amount) implements Serializable {

}

package com.paylinkfusion.payment.models.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paylinkfusion.payment.models.Transaction;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;



@Builder
public record PaymentRequestEvent(
        @JsonProperty("event_id")
        String eventID,
        @JsonProperty("created_date")
        Date createdDate,
        @JsonProperty("transaction")
        Transaction transaction) implements Serializable {

}

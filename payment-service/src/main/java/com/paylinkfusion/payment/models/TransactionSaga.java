package com.paylinkfusion.payment.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSaga {

    private String sagaId;

    private String transactionID;

    private String  accountID;

    private String sagaStatus;

    private String error;

    private Boolean isSuccessful;

}

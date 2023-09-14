package com.paylinkfusion.payment.models;


import com.paylinkfusion.payment.models.dto.AmountDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = "transaction")
public class Transaction {

    @Id
    private String id;

    private AmountDTO amount;

    @Indexed
    private String cardNumber;

    @Indexed
    private String cvv;

    @Indexed
    private String cardHolderName;

    @Indexed
    private Integer expiryMonth;

    @Indexed
    private Integer expiryYear;

    @Indexed
    private long accountID;

    @Indexed
    private String transactionStatus;

    @Indexed
    private long walletId;

    @Indexed
    private String error;

}

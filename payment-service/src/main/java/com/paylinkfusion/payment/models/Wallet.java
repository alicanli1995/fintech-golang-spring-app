package com.paylinkfusion.payment.models;

import com.paylinkfusion.payment.models.dto.AmountDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "wallet")
public class Wallet {

    @Id
    private String  id;

    @Indexed
    private AmountDTO amount;

    @Indexed(unique = true)
    private long accountID;

    @Indexed
    private String walletStatus;

}

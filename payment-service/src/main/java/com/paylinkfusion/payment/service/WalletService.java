package com.paylinkfusion.payment.service;

import com.paylinkfusion.payment.models.TransactionSaga;
import com.paylinkfusion.payment.models.Wallet;
import com.paylinkfusion.payment.models.dto.AmountDTO;

public interface WalletService {

    Wallet getWalletByAccountId(long accountId);

    void handleUserRegister(String userRegisterEvent);

    void chargeAmount(TransactionSaga transactionSaga, AmountDTO amount);
}

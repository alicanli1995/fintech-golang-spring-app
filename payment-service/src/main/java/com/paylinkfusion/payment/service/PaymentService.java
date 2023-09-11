package com.paylinkfusion.payment.service;

import com.paylinkfusion.payment.models.Transaction;
import com.paylinkfusion.payment.models.TransactionSaga;
import com.paylinkfusion.payment.models.dto.request.PaymentRequest;
import com.paylinkfusion.payment.models.dto.response.PaymentResponse;
import java.util.List;

public interface PaymentService {

    PaymentResponse handlePaymentRequest(PaymentRequest paymentRequest);

    List<Transaction> getUnProcessedTransactions();

    List<Transaction> getProcessingTransactions();

    void updateTransactionStatus(String id, String status);

    void updateResult(Transaction transaction, TransactionSaga transactionSaga);

    Transaction findByID(String id);

}

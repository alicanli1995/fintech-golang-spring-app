package com.paylinkfusion.payment.service;

import com.paylinkfusion.payment.models.Transaction;
import com.paylinkfusion.payment.models.TransactionSaga;
import java.util.List;

public interface SagaCouchbaseRepository {

    void createSagaRecord(Transaction paymentRequestEvent);

    List<TransactionSaga> findByTransactionID(String transactionID);

    void update(String sagaId, TransactionSaga transactionSaga);

    List<TransactionSaga> findAlreadySendNotifyRecords();
}

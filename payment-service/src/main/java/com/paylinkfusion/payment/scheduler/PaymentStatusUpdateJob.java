package com.paylinkfusion.payment.scheduler;

import com.paylinkfusion.payment.models.Transaction;
import com.paylinkfusion.payment.models.TransactionSaga;
import com.paylinkfusion.payment.models.dto.enums.SagaStatus;
import com.paylinkfusion.payment.service.PaymentService;
import com.paylinkfusion.payment.service.SagaCouchbaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusUpdateJob {

    private final PaymentService paymentService;
    private final SagaCouchbaseRepository sagaCouchbaseRepository;

    @Scheduled(cron = "0/5 0/1 * 1/1 * *")
    public void updatePaymentStatus() {
        final var getProcessingTransactions = paymentService.getProcessingTransactions();
        getProcessingTransactions.forEach(
                transaction -> sagaCouchbaseRepository.findByTransactionID(transaction.getId())
                        .stream()
                        .findFirst()
                        .ifPresent(transactionSaga -> handleTransactionStatus(transaction, transactionSaga)));
        log.info("Payment status updated. Total: {}", getProcessingTransactions.size());
    }

    private void handleTransactionStatus(Transaction transaction, TransactionSaga transactionSaga) {
        paymentService.updateResult(transaction, transactionSaga);
        handleSagaStatus(transactionSaga);
    }

    private void handleSagaStatus(TransactionSaga transactionSaga) {
        transactionSaga.setSagaStatus(SagaStatus.WAITING_NOTIFICATION.name());
        sagaCouchbaseRepository.update(transactionSaga.getSagaId(), transactionSaga);
    }
}

package com.paylinkfusion.payment.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.paylinkfusion.payment.exception.PaymentServiceException;
import com.paylinkfusion.payment.messaging.producer.PaymentRequestProducer;
import com.paylinkfusion.payment.models.dto.enums.TransactionStatus;
import com.paylinkfusion.payment.models.dto.request.PaymentRequestEvent;
import com.paylinkfusion.payment.service.PaymentService;
import com.paylinkfusion.payment.service.SagaCouchbaseRepository;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReqSenderKafkaJob {

    private final PaymentRequestProducer paymentRequestProducer;
    private final SagaCouchbaseRepository sagaCouchbaseRepository;
    private final PaymentService paymentService;


    @Scheduled(cron = "0/5 0/1 * 1/1 * *")
    public void sendPaymentRequest() {
        final var getUnProcessedTransactions = paymentService.getUnProcessedTransactions();
        getUnProcessedTransactions.forEach(transaction -> {
            PaymentRequestEvent paymentRequestEvent = PaymentRequestEvent.builder()
                    .eventID(UUID.randomUUID().toString()).createdDate(new Date()).transaction(transaction).build();
            try {
                paymentRequestProducer.sendOrderEventAsync(paymentRequestEvent);
                sagaCouchbaseRepository.createSagaRecord(transaction);
                paymentService.updateTransactionStatus(transaction.getId(),
                        TransactionStatus.PENDING_QUEUE.getStatus());

            } catch (JsonProcessingException e) {
                log.error("Error occurred while sending payment request event: {}", paymentRequestEvent, e);
                throw new PaymentServiceException("Error occurred while sending payment request event.",
                        HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
        });
    }

}

package com.paylinkfusion.payment.messaging.consumer;


import com.paylinkfusion.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterConsumer {

    private final WalletService walletService;

    @KafkaListener(topics = "user-register", groupId = "user-register-group")
    @RetryableTopic(
            attempts = "2",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        log.info("ConsumerRecord : {}", consumerRecord);
        walletService.handleUserRegister(consumerRecord.value());
    }

    @DltHandler
    public void dlt(String in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("DltHandler : {} from topic {}", in, topic);
    }


}

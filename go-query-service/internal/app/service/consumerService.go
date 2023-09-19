package service

import (
	"encoding/json"
	"fmt"
	"github.com/Trendyol/kafka-konsumer"
	"go-query-service/internal/app/models"
	"os"
	"os/signal"
	"time"
)

func SetupConsumerGroup() {

	consumerCfg := &kafka.ConsumerConfig{
		Concurrency: 1,
		Reader: kafka.ReaderConfig{
			Brokers: []string{"kafka-broker-1:9092"},
			Topic:   "payment-success",
			GroupID: "payment-service",
		},
		RetryEnabled: true,
		RetryConfiguration: kafka.RetryConfiguration{
			Brokers:       []string{"kafka-broker-1:9092"},
			Topic:         "retry-topic",
			StartTimeCron: "*/1 * * * *",
			WorkDuration:  50 * time.Second,
			MaxRetry:      3,
		},
		ConsumeFn: consumeFn,
	}

	consumer, _ := kafka.NewConsumer(consumerCfg)
	defer func(consumer kafka.Consumer) {
		_ = consumer.Stop()
	}(consumer)

	consumer.Consume()
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)

	<-c
}

func consumeFn(msg kafka.Message) error {
	var requestPayload models.TransactionSaga
	err := json.Unmarshal(msg.Value, &requestPayload)
	if err != nil {
		return fmt.Errorf("failed to unmarshal requestPayload: %w", err)
	}
	handler(requestPayload)
	return nil
}

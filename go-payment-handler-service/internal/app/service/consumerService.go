package service

import (
	"encoding/json"
	"fmt"
	"github.com/Trendyol/kafka-konsumer"
	"github.com/spf13/viper"
	"go-payment-handler-service/internal/app/models"
	"os"
	"os/signal"
	"time"
)

func SetupConsumerGroup() {

	consumerCfg := &kafka.ConsumerConfig{
		Concurrency: 1,
		Reader: kafka.ReaderConfig{
			Brokers: []string{viper.Get("kafka.consumer").(string)},
			Topic:   "payment-request",
			GroupID: "payment-service",
		},
		RetryEnabled: true,
		RetryConfiguration: kafka.RetryConfiguration{
			Brokers:       []string{viper.Get("kafka.consumer").(string)},
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
	var requestPayload models.PaymentRequestPayload
	err := json.Unmarshal(msg.Value, &requestPayload)
	if err != nil {
		return fmt.Errorf("failed to unmarshal requestPayload: %w", err)
	}
	_, _ = DoPaymentWithRequest(requestPayload)
	return nil
}

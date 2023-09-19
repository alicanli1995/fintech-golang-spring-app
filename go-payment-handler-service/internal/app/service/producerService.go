package service

import (
	"context"
	"encoding/json"
	"github.com/Trendyol/kafka-konsumer"
	"github.com/spf13/viper"
	"go-payment-handler-service/internal/app/models"
	"log"
)

var PaymentSuccessProducer kafka.Producer

func SetupProducer() {
	producer, _ := kafka.NewProducer(kafka.ProducerConfig{
		Writer: kafka.WriterConfig{
			Brokers: []string{viper.Get("kafka.producer").(string)},
		},
	})

	PaymentSuccessProducer = producer
}

const topicName = "payment-success"

func SendPaymentSuccessEvent(payload models.TransactionSaga) {
	payloadJson, _ := json.MarshalIndent(payload, "", "  ")

	err := PaymentSuccessProducer.Produce(context.Background(), kafka.Message{
		Topic: topicName,
		Key:   []byte(payload.SagaID),
		Value: payloadJson,
	})

	if err != nil {
		log.Println("failed to produce message: ", err)
	}
	log.Println("Payment Success Event Sent")
}

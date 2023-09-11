package service

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/couchbase/gocb/v2"
	"github.com/spf13/viper"
	"go-payment-handler-service/internal/app/models"
	db "go-payment-handler-service/internal/app/repository"
	dbrepo "go-payment-handler-service/internal/app/repository/dprepo"
	"io"
	"log"
	"net/http"
)

var Repo *Repository

var headers HeadersConfig

type HeadersConfig struct {
	XPaymentsOsEnv string
	ApiVersion     string
	PublicKey      string `mapstructure:"PAYMENT_PUBLIC_KEY"`
	PrivateKey     string `mapstructure:"PAYMENT_PRIVATE_KEY"`
	AppID          string `mapstructure:"PAYMENT_APP_ID"`
}

type Repository struct {
	DB db.CouchRepo
}

func NewRepository(cluster gocb.Cluster, bucket gocb.Bucket) *Repository {
	return &Repository{
		DB: dbrepo.NewCouchbaseRepository(cluster, bucket),
	}
}

func NewHandlers(r *Repository) {
	Repo = r
}

func completePayment(paymentID string, token string, payload models.PaymentRequestPayload) (string, error) {
	url := fmt.Sprintf("https://api.paymentsos.com/payments/%s/charges", paymentID)

	var chargeRequestPayload models.ChargeRequest
	chargeRequestPayload.PaymentMethod.Token = token
	chargeRequestPayload.PaymentMethod.Type = "tokenized"
	chargeRequestPayload.PaymentMethod.CreditCardCvv = payload.Transaction.CVV

	out, err := json.Marshal(chargeRequestPayload)
	if err != nil {
		_ = fmt.Sprintf("failed to marshal chargeRequestPayload: %v", err)
		return "", err
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(out))
	if err != nil {
		_ = fmt.Sprintf("failed to create new request: %v", err)
		return "", err
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("x-payments-os-env", headers.XPaymentsOsEnv)
	req.Header.Set("api-version", headers.ApiVersion)
	req.Header.Set("private-key", headers.PrivateKey)
	req.Header.Set("app_id", headers.AppID)

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		_ = fmt.Sprintf("failed to do request: %v", err)
		return "", err
	}

	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			_ = fmt.Sprintf("failed to close body: %v", err)
		}
	}(resp.Body)

	log.Println("Transaction ID: {}, and completePayment status is: {}", payload.ID, resp.Status)

	var paymentResponsePayload models.ChargeResponse
	err = json.NewDecoder(resp.Body).Decode(&paymentResponsePayload)
	if err != nil {
		_ = fmt.Sprintf("failed to decode response: %v", err)
		return "", err
	}

	if paymentResponsePayload.Result.Status == "Succeed" {
		return paymentResponsePayload.ID, nil
	} else {
		return "", fmt.Errorf("payment failed: %v", paymentResponsePayload.Result.Status)
	}

}

func createPaymentDemand(payload models.PaymentRequestPayload) (string, error) {
	url := "https://api.paymentsos.com/payments"
	var paymentRequestPayload models.PaymentRequest
	paymentRequestPayload.Amount = payload.Transaction.Amount.Amount * 100
	paymentRequestPayload.Currency = payload.Transaction.Amount.Currency

	out, err := json.Marshal(paymentRequestPayload)
	if err != nil {
		_ = fmt.Sprintf("failed to marshal paymentRequestPayload: %v", err)
		return "", err
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(out))
	if err != nil {
		_ = fmt.Sprintf("failed to create new request: %v", err)
		return "", err
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("x-payments-os-env", headers.XPaymentsOsEnv)
	req.Header.Set("api-version", headers.ApiVersion)
	req.Header.Set("private-key", headers.PrivateKey)
	req.Header.Set("app_id", headers.AppID)

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		_ = fmt.Sprintf("failed to do request: %v", err)
		return "", err
	}

	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			_ = fmt.Sprintf("failed to close body: %v", err)
		}
	}(resp.Body)

	log.Println("Transaction ID: {}, and createPaymentDemand status is: {}", payload.ID, resp.Status)

	var paymentResponsePayload models.PaymentResponse
	err = json.NewDecoder(resp.Body).Decode(&paymentResponsePayload)
	if err != nil {
		_ = fmt.Sprintf("failed to decode response: %v", err)
		return "", err
	}

	return paymentResponsePayload.ID, nil
}

func createTokenForPayment(payload models.PaymentRequestPayload) (models.TokenResponse, error) {
	url := "https://api.paymentsos.com/tokens"
	var tokenRequestPayload models.TokenRequest
	tokenRequestPayload.CardNumber = payload.Transaction.CardNumber
	tokenRequestPayload.CVV = payload.Transaction.CVV
	tokenRequestPayload.ExpireDate = fmt.Sprintf("%d/%d", payload.Transaction.ExpireMonth, payload.Transaction.ExpireYear)
	tokenRequestPayload.TokenType = "credit_card"
	tokenRequestPayload.HolderName = payload.Transaction.HolderName

	out, err := json.Marshal(tokenRequestPayload)
	if err != nil {
		_ = fmt.Sprintf("failed to marshal tokenRequestPayload: %v", err)
		return models.TokenResponse{}, err
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(out))
	if err != nil {
		_ = fmt.Sprintf("failed to create new request: %v", err)
		return models.TokenResponse{}, err
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("x-payments-os-env", headers.XPaymentsOsEnv)
	req.Header.Set("api-version", headers.ApiVersion)
	req.Header.Set("public-key", headers.PublicKey)

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		_ = fmt.Sprintf("failed to do request: %v", err)
		return models.TokenResponse{}, err
	}

	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			_ = fmt.Sprintf("failed to close body: %v", err)
		}
	}(resp.Body)

	log.Println("Transaction ID: {}, and createTokenForPayment status is: {}", payload.ID, resp.Status)

	var tokenResponsePayload models.TokenResponse
	err = json.NewDecoder(resp.Body).Decode(&tokenResponsePayload)
	if err != nil {
		_ = fmt.Sprintf("failed to decode response: %v", err)
		return models.TokenResponse{}, err
	}

	return tokenResponsePayload, nil
}

func DoPaymentWithRequest(payload models.PaymentRequestPayload) (models.PaymentResponsePayload, error) {
	initHeaders()

	token, err := createTokenForPayment(payload)
	if err != nil {
		handleSagaStatus(payload, err)
		return models.PaymentResponsePayload{}, fmt.Errorf("failed to create token for payment: %w", err)
	}
	paymentID, err := createPaymentDemand(payload)
	if err != nil {
		handleSagaStatus(payload, err)
		return models.PaymentResponsePayload{}, fmt.Errorf("failed to create payment demand: %w", err)
	}

	payment, err := completePayment(paymentID, token.Token, payload)
	if err != nil {
		handleSagaStatus(payload, err)
		return models.PaymentResponsePayload{}, fmt.Errorf("failed to complete payment: %w", err)
	}

	handleSagaStatus(payload, err)

	if payment != "" {
		return models.PaymentResponsePayload{
			ID:        payload.ID,
			CreatedAt: payload.CreatedAt,
			Status:    "Succeed",
			Message:   "Payment is successful",
		}, nil
	} else {
		return models.PaymentResponsePayload{
			ID:        payload.ID,
			CreatedAt: payload.CreatedAt,
			Status:    "Failed",
			Message:   "Payment is failed",
		}, nil

	}
}

func handleSagaStatus(payload models.PaymentRequestPayload, err error) {
	sagaRecord, err := Repo.DB.QueryTransactionSaga(payload.Transaction.ID)
	if err != nil || len(sagaRecord) == 0 {
		log.Printf("failed to query transaction saga: %v", err)
	}
	saga := sagaRecord[0]

	var sagaStatus string
	var e string
	if err != nil {
		sagaStatus = "FAILED"
		e = err.Error()
	} else {
		sagaStatus = "COMPLETED"
		e = ""
		SendPaymentSuccessEvent(saga)
	}
	saga.SagaStatus = sagaStatus
	saga.Error = e
	saga.IsSuccessful = true
	err = Repo.DB.UpdateTransactionSaga(saga)
	if err != nil {
		log.Printf("failed to update transaction saga: %v", err)
	}

	log.Printf("saga status is updated successfully for transaction: %v", payload.Transaction.ID)
}

func initHeaders() {
	headers.XPaymentsOsEnv = viper.Get("payment-os.header.x-payments-os-env").(string)
	headers.ApiVersion = viper.Get("payment-os.header.api-version").(string)
	headers.PublicKey = viper.Get("PAYMENT_PUBLIC_KEY").(string)
	headers.PrivateKey = viper.Get("PAYMENT_PRIVATE_KEY").(string)
	headers.AppID = viper.Get("PAYMENT_APP_ID").(string)
}

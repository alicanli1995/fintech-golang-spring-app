package models

import "time"

type PaymentRequestPayload struct {
	ID          string      `json:"event_id"`
	CreatedAt   time.Time   `json:"created_date"`
	Transaction Transaction `json:"transaction"`
}

type Transaction struct {
	ID          string `json:"id"`
	Amount      Amount `json:"amount"`
	CardNumber  string `json:"cardNumber"`
	CVV         string `json:"cvv"`
	HolderName  string `json:"cardHolderName"`
	ExpireMonth int    `json:"expiryMonth"`
	ExpireYear  int    `json:"expiryYear"`
	AccountID   int    `json:"accountID"`
	WalletID    int    `json:"walletId"`
}

type Amount struct {
	Currency string  `json:"currency"`
	Amount   float32 `json:"amount"`
}

type TokenRequest struct {
	CardNumber string `json:"card_number"`
	CVV        string `json:"credit_card_cvv"`
	ExpireDate string `json:"expiration_date"`
	TokenType  string `json:"token_type"`
	HolderName string `json:"holder_name"`
}

type TokenResponse struct {
	Token     string `json:"token"`
	Created   string `json:"created"`
	TokenType string `json:"token_type"`
	Type      string `json:"type"`
}

type PaymentRequest struct {
	Amount   float32 `json:"amount"`
	Currency string  `json:"currency"`
}

type PaymentResponse struct {
	ID       string `json:"id"`
	Status   string `json:"status"`
	Currency string `json:"currency"`
	Amount   int    `json:"amount"`
}

type PaymentResponsePayload struct {
	ID        string    `json:"event_id"`
	CreatedAt time.Time `json:"created_date"`
	Status    string    `json:"status"`
	Message   string    `json:"message"`
}

type ChargeRequest struct {
	PaymentMethod PaymentMethod `json:"payment_method"`
}

type PaymentMethod struct {
	Token         string `json:"token"`
	Type          string `json:"type"`
	CreditCardCvv string `json:"credit_card_cvv"`
}

type ChargeResponse struct {
	ID     string `json:"id"`
	Result Result `json:"result"`
	Amount int    `json:"amount"`
}

type Result struct {
	Status      string `json:"status"`
	Category    string `json:"category,omitempty"`
	Description string `json:"description,omitempty"`
}

type TransactionSaga struct {
	SagaID        string `json:"sagaId"`
	TransactionID string `json:"transactionID"`
	AccountID     string `json:"accountID"`
	SagaStatus    string `json:"sagaStatus"`
	Error         string `json:"error"`
	IsSuccessful  bool   `json:"isSuccessful"`
}

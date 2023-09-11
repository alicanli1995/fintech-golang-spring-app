package models

type TransactionSaga struct {
	SagaID        string `json:"sagaId"`
	TransactionID string `json:"transactionID"`
	AccountID     string `json:"accountID"`
	SagaStatus    string `json:"sagaStatus"`
	Error         string `json:"error"`
	IsSuccessful  bool   `json:"isSuccessful"`
}

type Leaderboard struct {
	AccountID string  `json:"accountID"`
	Amount    float64 `json:"amount"`
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
	Currency string `json:"currency"`
	Amount   string `json:"amount"`
}

type Lead struct {
	AccountID string `json:"accountID"`
	Amount    string `json:"amount"`
}

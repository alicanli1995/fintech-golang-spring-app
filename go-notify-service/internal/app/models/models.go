package models

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
	Amount   int    `json:"amount"`
}

type TransactionSaga struct {
	SagaID        string `json:"sagaId"`
	TransactionID string `json:"transactionID"`
	AccountID     string `json:"accountID"`
	SagaStatus    string `json:"sagaStatus"`
	Error         string `json:"error"`
	IsSuccessful  bool   `json:"isSuccessful"`
}

type Account struct {
	ID    int    `json:"id"`
	Email string `json:"email"`
	Name  string `json:"full_name"`
}

// MailData holds data used when sending emails
type MailData struct {
	To       string
	From     string
	Subject  string
	Content  string
	Template string
}

package dbrepo

import (
	"go-payment-handler-service/internal/app/models"
)

type CouchRepo interface {
	GetTransactionSaga(sagaID string) (*models.TransactionSaga, error)
	InsertTransactionSaga(saga models.TransactionSaga) error
	QueryTransactionSaga(transactionID string) ([]models.TransactionSaga, error)
	QueryTransactionStatusSaga(status string) ([]models.TransactionSaga, error)
	UpdateTransactionSaga(saga models.TransactionSaga) error
}

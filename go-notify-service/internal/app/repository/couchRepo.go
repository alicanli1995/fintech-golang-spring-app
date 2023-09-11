package dbrepo

import (
	"go-notify-service/internal/app/models"
)

type CouchRepo interface {
	GetTransactionSaga(sagaID string) (*models.TransactionSaga, error)
	QueryTransactionStatusSaga(status string) ([]models.TransactionSaga, error)
	UpdateTransactionSaga(saga models.TransactionSaga) error
}

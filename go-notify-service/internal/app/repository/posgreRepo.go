package dbrepo

import (
	"go-notify-service/internal/app/models"
)

type PostgresRepo interface {
	GetAccountByID(id int) (*models.Account, error)
}

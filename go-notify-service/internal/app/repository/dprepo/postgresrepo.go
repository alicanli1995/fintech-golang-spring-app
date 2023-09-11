package dprepo

import (
	"errors"
	"go-notify-service/internal/app/models"
)

func (p PostgresRepository) GetAccountByID(id int) (*models.Account, error) {
	// Get account by ID
	var account models.Account

	err := p.Conn.QueryRow("SELECT id, email, full_name FROM account "+
		"WHERE id = $1", id).Scan(&account.ID, &account.Email, &account.Name)

	if err != nil {
		return nil, errors.New("account not found")
	}

	return &account, nil
}

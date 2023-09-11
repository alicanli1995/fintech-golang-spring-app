package dprepo

import (
	"database/sql"
	"go-notify-service/internal/app/repository"
)

var db *sql.DB

type PostgresRepository struct {
	Conn *sql.DB
}

func NewPostgresRepository(pool *sql.DB) dbrepo.PostgresRepo {
	db = pool
	return &PostgresRepository{
		Conn: db,
	}
}

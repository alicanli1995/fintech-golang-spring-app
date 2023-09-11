package service

import (
	"database/sql"
	"fmt"
	"github.com/couchbase/gocb/v2"
	"go-notify-service/config"
	"go-notify-service/internal/app/models"
	dbrepo2 "go-notify-service/internal/app/repository"
	"go-notify-service/internal/app/repository/dprepo"

	"log"
	"strconv"
)

var Repo *Repository

type Repository struct {
	DB  dbrepo2.CouchRepo
	SQL dbrepo2.PostgresRepo
	App *config.AppConfig
}

func NewRepository(cluster gocb.Cluster, bucket gocb.Bucket, postgres *sql.DB, app *config.AppConfig) *Repository {
	return &Repository{
		DB:  dprepo.NewCouchbaseRepository(cluster, bucket),
		SQL: dprepo.NewPostgresRepository(postgres),
		App: app,
	}
}

func NewHandlers(r *Repository) {
	Repo = r
}

func SendNotify() {
	notifyList, err := Repo.DB.QueryTransactionStatusSaga("WAITING_NOTIFICATION")
	if err != nil {
		log.Printf("Don't have any transaction need to notify")
	}

	for _, notify := range notifyList {
		log.Printf("Notify transaction: %s", notify.SagaID)
		accID, _ := strconv.Atoi(notify.AccountID)
		account, err := Repo.SQL.GetAccountByID(accID)
		if err != nil {
			log.Printf("Can't get account by id: %s", notify.AccountID)
			return
		}

		mail := handleNotify(account, notify)
		Repo.App.MailChan <- *mail

		if notify.IsSuccessful {
			notify.SagaStatus = "TRANSACTION_COMPLETED"
		} else {
			notify.SagaStatus = "TRANSACTION_FAILED"
		}

		err = Repo.DB.UpdateTransactionSaga(notify)
		if err != nil {
			log.Printf("Can't update transaction saga: %s", notify.SagaID)
		}
	}

	log.Printf("Notify sending operation completed")

}

func handleNotify(account *models.Account, notify models.TransactionSaga) *models.MailData {
	var mail models.MailData
	mail.From = "admin@paylink-fusion.com"
	mail.Subject = "Payment Result Summary"
	mail.To = account.Email
	mail.Template = "basic.html"
	mail.Content = getMessage(notify, account)

	return &mail
}

func getMessage(payment models.TransactionSaga, acc *models.Account) string {
	var sagaStatus string
	if payment.IsSuccessful {
		sagaStatus = "Success"
	} else {
		sagaStatus = "Fail"
	}
	htmlMessage := fmt.Sprintf(`
		<strong>Your payment charge summary </strong><br>
		<strong>Transaction ID: </strong> %s <br>
		<strong>Account ID: </strong> %s <br>
		<strong>Payment Status: </strong> %s <br>
		`, payment.TransactionID, acc.ID, sagaStatus)

	return htmlMessage
}

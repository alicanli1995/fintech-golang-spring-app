package main

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/couchbase/gocb/v2"
	"github.com/go-co-op/gocron"
	_ "github.com/jackc/pgconn"
	_ "github.com/jackc/pgx/v4"
	_ "github.com/jackc/pgx/v4/stdlib"
	"github.com/spf13/viper"
	"go-notify-service/config"
	"go-notify-service/internal/app/models"
	"go-notify-service/internal/app/service"
	"log"
	"os"
	"time"
)

var appConfig config.AppConfig

var counts int64

func main() {
	env := os.Getenv("ENV")
	configParse(env)
	initDatabaseOperations()
	initScheduler()

	mailChan := make(chan models.MailData)
	appConfig.MailChan = mailChan
	defer close(appConfig.MailChan)
	mailChannelListener()

	log.Println("Setup completed and ready to use, Notification Service is running... 🚀 🚀 🚀")
	select {}
}

func mailChannelListener() {

	go func() {
		for {
			// Read the next email from the channel
			msg := <-appConfig.MailChan
			// Send the email
			service.SendMail(msg)
		}
	}()
}

func initScheduler() {
	s := gocron.NewScheduler(time.UTC)
	do, err := s.Every(5).Second().Do(service.SendNotify)
	if err != nil {
		return
	}
	do.Tag("notify-scheduler")

	s.StartAsync()

	log.Println("Scheduler setup completed and ready to use... 🚀 🚀 🚀")
}

func initDatabaseOperations() {

	configMap := viper.GetStringMapString("couchbase")

	connectionString := configMap["host"]
	bucketName := configMap["bucket"]
	username := configMap["username"]
	password := configMap["password"]

	options := gocb.ClusterOptions{
		Authenticator: gocb.PasswordAuthenticator{
			Username: username,
			Password: password,
		},
	}

	cluster, err := gocb.Connect("couchbase://"+connectionString, options)
	if err != nil {
		log.Fatal(err)
	}

	bucket := cluster.Bucket(bucketName)

	err = bucket.WaitUntilReady(5*time.Second, nil)
	if err != nil {
		log.Fatal(err)
	}

	db := connectToDB()

	repository := service.NewRepository(*cluster, *bucket, db, &appConfig)
	service.NewHandlers(repository)

	log.Println("Couchbase setup completed and ready to use... 🚀 🚀 🚀")
}

func connectToDB() *sql.DB {
	dsn := viper.GetString("postgres.dsn")

	for {
		conn, err := openDB(dsn)
		if err != nil {
			log.Println("Error connecting to database", err)
			counts++
		} else {
			log.Println("Connected to database")
			return conn
		}

		if counts > 10 {
			log.Println("Too many attempts to connect to database")
			os.Exit(1)
		}

		log.Println("Waiting to connect to database")
		time.Sleep(2 * time.Second)
		continue
	}

}

func openDB(dsn string) (*sql.DB, error) {
	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}

	if err = db.Ping(); err != nil {
		log.Println("Error pinging database", err)
		return nil, err
	}

	return db, nil
}

func configParse(env string) {
	x := viper.New()
	if env == "DOCKER" {
		x.SetConfigName("app")
	} else {
		x.SetConfigName("app-local")
	}
	x.SetConfigType("yaml")
	x.AddConfigPath("./config")
	x.AddConfigPath("../app/config")

	if err := x.ReadInConfig(); err != nil {
		var configFileNotFoundError viper.ConfigFileNotFoundError
		if errors.As(err, &configFileNotFoundError) {
			fmt.Println("Config file not found... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️")
		}
	}

	viper.AutomaticEnv()
	_ = viper.MergeConfigMap(x.AllSettings())

	log.Println("Config file loaded successfully... all systems go! 🚀 🚀 🚀")
}

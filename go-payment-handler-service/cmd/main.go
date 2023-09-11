package main

import (
	"errors"
	"fmt"
	"github.com/couchbase/gocb/v2"
	"github.com/spf13/viper"
	"go-payment-handler-service/internal/app/service"
	"log"
	"time"
)

func main() {

	configParse()
	initCouchbase()

	go service.SetupConsumerGroup()
	go service.SetupProducer()
	select {}
}

func initCouchbase() {

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

	repository := service.NewRepository(*cluster, *bucket)
	service.NewHandlers(repository)

	log.Println("Couchbase setup completed and ready to use... 🚀 🚀 🚀")
}

func configParse() {
	x := viper.New()
	y := viper.New()
	x.SetConfigName("app")
	x.SetConfigType("yaml")
	x.AddConfigPath("./config")

	if err := x.ReadInConfig(); err != nil {
		var configFileNotFoundError viper.ConfigFileNotFoundError
		if errors.As(err, &configFileNotFoundError) {
			fmt.Println("Config file not found... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️")
		}
	}

	y.AddConfigPath(".")
	y.SetConfigName("app")
	y.SetConfigFile("app.env")
	y.SetConfigType("env")
	if err := y.ReadInConfig(); err != nil {
		log.Fatal("Error reading env file", err)
	}

	viper.AutomaticEnv()
	_ = viper.MergeConfigMap(x.AllSettings())
	_ = viper.MergeConfigMap(y.AllSettings())

	log.Println("Config file loaded successfully... all systems go! 🚀 🚀 🚀")
}

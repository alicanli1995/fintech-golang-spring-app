package main

import (
	"errors"
	"fmt"
	"github.com/couchbase/gocb/v2"
	"github.com/spf13/viper"
	"go-payment-handler-service/internal/app/service"
	"log"
	"os"
	"time"
)

func main() {

	env := os.Getenv("ENV")

	configParse(env)
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

func configParse(env string) {
	x := viper.New()
	y := viper.New()

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
			fmt.Println("Config file not found... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️, fileName: app.yaml")
		} else {
			fmt.Println("Config file read error... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️, fileName: app.yaml")
		}
	}

	y.AddConfigPath("./config")
	y.AddConfigPath("../app/config")
	y.SetConfigName("secret")
	y.SetConfigType("env")

	if err := y.ReadInConfig(); err != nil {
		var configFileNotFoundError viper.ConfigFileNotFoundError
		if errors.As(err, &configFileNotFoundError) {
			fmt.Println("Config file not found... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️, fileName: secret.env")
		} else {
			fmt.Println("Config file read error... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️, fileName: secret.env")
		}
	}

	viper.AutomaticEnv()
	_ = viper.MergeConfigMap(x.AllSettings())
	_ = viper.MergeConfigMap(y.AllSettings())

	log.Println("Config file loaded successfully... all systems go! 🚀 🚀 🚀")
}

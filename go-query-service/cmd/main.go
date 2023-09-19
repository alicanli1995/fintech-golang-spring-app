package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/spf13/viper"
	"go-query-service/internal/app/service"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"log"
)

func main() {

	go configParse()
	go service.NewRedisService()
	go service.SetupConsumerGroup()
	go initMongo()
	go routes()
	select {}
}

func initMongo() {
	var ctx = context.TODO()

	clientOptions := options.Client().ApplyURI("mongodb://mongo:27017/")
	client, err := mongo.Connect(ctx, clientOptions)
	if err != nil {
		log.Fatal(err)
	}

	err = client.Ping(ctx, nil)
	if err != nil {
		log.Fatal("MongoDB connection error: ", err)
	}

	fmt.Println("MongoDB connections established successfully... 🚀 🚀 🚀")

	defer func() {
		if err = client.Disconnect(ctx); err != nil {
			log.Fatal(err)
		}
	}()

	collection := client.Database("test").Collection("transaction")

	service.InitMongo(&collection)

	select {}
}

func configParse() {
	x := viper.New()
	x.SetConfigName("app")
	x.SetConfigType("yaml")
	x.AddConfigPath("./config")

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

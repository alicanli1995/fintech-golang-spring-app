package service

import (
	"context"
	"errors"
	"fmt"
	"go-query-service/internal/app/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"log"
	"strconv"
)

var mongoCollection *mongo.Collection

func InitMongo(collection **mongo.Collection) {
	mongoCollection = *collection
}

func handler(payload models.TransactionSaga) {
	if payload.AccountID == "" || payload.TransactionID == "" {
		return
	}
	leaderBoardRecord := models.Leaderboard{
		AccountID: payload.AccountID,
		Amount:    getAmount(payload.TransactionID),
	}

	UpdateOrAddLeaderScore(leaderBoardRecord.AccountID, leaderBoardRecord.Amount)
}

func getAmount(tranID string) float64 {
	id, err := primitive.ObjectIDFromHex(tranID)
	if err != nil {
		log.Println(err)
	}

	var result models.Transaction

	res := mongoCollection.FindOne(context.Background(), bson.M{"_id": id})
	if res.Err() != nil {
		if errors.As(res.Err(), &mongo.ErrNoDocuments) {
			fmt.Println("Document not found... 🤷‍♂️ 🤷‍♂️ 🤷‍♂️")
		} else {
			fmt.Println("Error occurred: ", res.Err())
		}
	}

	err = res.Decode(&result)
	if err != nil {
		log.Fatal(err)
	}

	amount, err := strconv.ParseFloat(result.Amount.Amount, 32)
	if err != nil {
		log.Fatal(err)
	}
	return amount
}

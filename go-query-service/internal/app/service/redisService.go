package service

import (
	"context"
	"fmt"
	"github.com/redis/go-redis/v9"
	"go-query-service/internal/app/models"
	"log"
)

var RedisService *redis.Client

func NewRedisService() {
	rdb := redis.NewClient(&redis.Options{
		Addr:     "redis:6379",
		Password: "",
		DB:       0,
	})

	RedisService = rdb

	log.Println("Redis setup completed and ready to use... 🚀 🚀 🚀")
	select {}

}

func UpdateOrAddLeaderScore(accountID string, amount float64) {
	score := GetLeaderBoardRecordScore(accountID)
	if score > 0 {
		amount += score
	}

	RedisService.ZAdd(context.Background(), "leaderboard", redis.Z{
		Score:  amount,
		Member: accountID,
	})

	log.Println("Leaderboard updated")
}

func GetLeaderBoardRecordScore(accountID string) float64 {
	score, err := RedisService.ZScore(context.Background(), "leaderboard", accountID).Result()
	if err != nil {
		return 0
	}

	return score
}

func GetLeaderboard(start, end int64) []*models.Lead {
	leaderboard, err := RedisService.ZRevRangeWithScores(context.Background(), "leaderboard", start, end).Result()
	if err != nil {
		log.Fatalf("Error occurred while getting leaderboard: %v", err)
	}

	var leads []*models.Lead

	for _, item := range leaderboard {
		var lead models.Lead

		lead.AccountID = item.Member.(string)
		lead.Amount = fmt.Sprintf("%.2f", item.Score)

		leads = append(leads, &lead)
	}

	return leads
}

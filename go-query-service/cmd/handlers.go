package main

import (
	"github.com/gin-gonic/gin"
	"go-query-service/internal/app/service"
)

func GetLeadFromRedis() gin.H {
	leaderboard := service.GetLeaderboard(0, 10)

	return gin.H{
		"leaderboard": leaderboard,
	}

}

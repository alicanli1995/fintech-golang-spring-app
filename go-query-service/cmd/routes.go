package main

import "github.com/gin-gonic/gin"

func routes() {
	r := gin.Default()

	r.GET("/get-leads", func(c *gin.Context) {
		c.JSON(200, GetLeadFromRedis())
	})

	err := r.Run(":1903")
	if err != nil {
		return
	}
}

package service

import (
	"fmt"
	mail "github.com/xhit/go-simple-mail/v2"
	"go-notify-service/internal/app/models"
	"log"
	"os"
	"strings"
	"time"
)

func SendMail(msg models.MailData) {
	server := getMailConfig()
	client, err := server.Connect()
	if err != nil {
		log.Println(err)
		return
	}
	email := mail.NewMSG()
	email.SetFrom(msg.From).AddTo(msg.To).SetSubject(msg.Subject)
	if msg.Template == "" {
		email.SetBody(mail.TextHTML, msg.Content)
	} else {
		data, err := os.ReadFile(fmt.Sprintf("./email-templates/%s", msg.Template))
		if err != nil {
			log.Println(err)
			return
		}
		mailTemplate := string(data)
		msgToSend := strings.Replace(mailTemplate, "[%body%]", msg.Content, 1)
		email.SetBody(mail.TextHTML, msgToSend)
	}
	err = email.Send(client)
	if err != nil {
		log.Println(err)
		return
	} else {
		log.Println("Mail sent successfully")
	}
}

func getMailConfig() *mail.SMTPServer {
	server := mail.NewSMTPClient()
	server.Host = "localhost"
	server.Port = 1025
	server.KeepAlive = false
	server.ConnectTimeout = 10 * time.Second
	server.SendTimeout = 10 * time.Second
	return server
}

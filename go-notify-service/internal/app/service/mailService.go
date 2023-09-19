package service

import (
	"fmt"
	"github.com/spf13/viper"
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
			data, err = os.ReadFile(fmt.Sprintf("../app/email-templates/%s", msg.Template))
			if err != nil {
				log.Fatal("Can't read email template file")
			}
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
	server.Host = viper.Get("mail.host").(string)
	server.Port = 1025
	server.KeepAlive = false
	server.ConnectTimeout = 10 * time.Second
	server.SendTimeout = 10 * time.Second
	return server
}

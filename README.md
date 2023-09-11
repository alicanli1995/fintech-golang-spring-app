# PAYLINK FUSION PROJECT

## Introduction

This project is a simple payment system that allows users to charge own wallet.<br>
Mainly idea learning how to use GoLang, Spring Boot working together.<br>
Some services written in GoLang, some in Java.<br>
Generally Go and Java services communicate with each other using Kafka.<br>
GoLang services use MongoDB, Redis, Couchbase, PostgreSQL.<br>
Java services use PostgreSQL, MongoDB, Couchbase.<br>

## Architecture

![Architecture](img/diagram.png)

## How to run

To be implemented

## Prerequisites

- Docker
- Docker Compose
- Kafka
- MongoDB
- PostgresSQL
- Couchbase
- Java 17
- Maven
- GoLang
- Kafka
- Redis

### Run

1. Run Kafka
2. Run PostgreSQL & MongoDB & Couchbase
3. Run MailHog for Mock SMTP Server
4. Run GoLang services
5. Run Java services

## Attention

- You can run all services with docker-compose.yml file. But you need to change some environment
  variables.
- Like as PaymentOS Credentials: `PAYMENT_PUBLIC_KEY` & `PAYMENT_PRIVATE_KEY` and `PAYMENT_APP_ID`.
  You can change these variables in go-payment-handler-service
- and open app.env file change your credentials.

#### Run Infrastructure

To be implemented

### What's next?

- [ ] Implementing Docker Compose
- [ ] Implementing Kubernetes
- [ ] Implementing CI/CD
- [ ] Implementing Unit Tests
- [ ] Implementing Integration Tests
- [ ] Starting to write documentation
- [ ] Starting to frontend development

## Services

### GoLang

- [x] [Payment Handler](#!)
- [x] [Notification Handler](#!)
- [x] [Query Service](#!)

### Java

- [x] [Auth & Gateway](#!)
- [x] [Payment Service](#!)

## Authors

- [x] [Ali CANLI](#!)




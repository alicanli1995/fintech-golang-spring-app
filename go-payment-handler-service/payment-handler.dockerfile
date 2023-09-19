# base go image
FROM golang:1.21-alpine AS builder

RUN mkdir /app

COPY . /app

WORKDIR /app

RUN CGO_ENABLED=0 go build -o paymentHandler ./cmd/

# build a tiny image
FROM golang:1.21-alpine
#
RUN mkdir /app

COPY --from=builder /app/paymentHandler /app
COPY . /app

CMD ["/app/paymentHandler"]
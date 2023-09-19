# base go image
FROM golang:1.21-alpine AS builder

RUN mkdir /app

COPY . /app

WORKDIR /app

RUN CGO_ENABLED=0 go build -o notificationHandler ./cmd/

# build a tiny image
FROM golang:1.21-alpine
#
RUN mkdir /app

COPY --from=builder /app/notificationHandler /app
COPY . /app

CMD ["/app/notificationHandler"]
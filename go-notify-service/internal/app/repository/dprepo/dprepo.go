package dprepo

import (
	"fmt"
	"go-notify-service/internal/app/models"
)

func (r *CouchbaseRepository) GetTransactionSaga(sagaID string) (*models.TransactionSaga, error) {
	col := r.Bucket.Scope("_default").Collection("_default")

	getResult, err := col.Get(sagaID, nil)
	if err != nil {
		return nil, err
	}

	var transactionSaga models.TransactionSaga
	err = getResult.Content(&transactionSaga)
	if err != nil {
		return nil, err
	}

	return &transactionSaga, nil
}

func (r *CouchbaseRepository) UpdateTransactionSaga(saga models.TransactionSaga) error {
	col := r.Bucket.Scope("_default").Collection("_default")

	_, err := col.Replace(saga.SagaID, saga, nil)
	if err != nil {
		return err
	}

	return nil
}

func (r *CouchbaseRepository) QueryTransactionStatusSaga(status string) ([]models.TransactionSaga, error) {
	queryResult, err := r.Cluster.Query(
		fmt.Sprintf("SELECT p.* FROM `payment`.`_default`.`_default` p WHERE p.sagaStatus='%s'", status), nil)
	if err != nil {
		return nil, err
	}

	var results []models.TransactionSaga

	for queryResult.Next() {
		var result models.TransactionSaga
		err := queryResult.Row(&result)
		if err != nil {
			return nil, err
		}
		results = append(results, result)
	}

	if err := queryResult.Err(); err != nil {
		return nil, err
	}

	return results, nil
}

package dprepo

import (
	"github.com/couchbase/gocb/v2"
	"go-payment-handler-service/internal/app/repository"
)

type CouchbaseRepository struct {
	Cluster *gocb.Cluster
	Bucket  *gocb.Bucket
}

func NewCouchbaseRepository(cluster gocb.Cluster, bucket gocb.Bucket) dbrepo.CouchRepo {
	return &CouchbaseRepository{
		Cluster: &cluster,
		Bucket:  &bucket,
	}
}

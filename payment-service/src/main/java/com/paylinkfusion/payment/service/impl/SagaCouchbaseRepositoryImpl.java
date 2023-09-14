package com.paylinkfusion.payment.service.impl;

import static com.paylinkfusion.payment.configs.CollectionNames.PROFILE;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.InsertOptions;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import com.paylinkfusion.payment.configs.DBProperties;
import com.paylinkfusion.payment.models.Transaction;
import com.paylinkfusion.payment.models.TransactionSaga;
import com.paylinkfusion.payment.models.dto.enums.SagaStatus;
import com.paylinkfusion.payment.service.SagaCouchbaseRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SagaCouchbaseRepositoryImpl implements SagaCouchbaseRepository {

    private Collection collection;
    private Cluster cluster;
    private DBProperties dbProperties;


    public SagaCouchbaseRepositoryImpl(Bucket bucket, Cluster cluster, DBProperties dbProperties) {
        this.collection = bucket.collection(PROFILE);
        this.cluster = cluster;
        this.dbProperties = dbProperties;
    }

    @Override
    public void createSagaRecord(Transaction transaction) {
        TransactionSaga transactionSaga = getTransactionSaga(transaction);

        collection.insert(transactionSaga.getSagaId(), transactionSaga,
                InsertOptions.insertOptions().timeout(Duration.ofMinutes(2)));
        log.info("Saga record created for transactionID: {}", transaction.getId());
    }

    @Override
    public List<TransactionSaga> findByTransactionID(String transactionID) {
        String qryString = "SELECT p.* FROM `" + dbProperties.getBucketName() + "`.`_default`.`" + PROFILE + "` p "
                + "WHERE p.transactionID LIKE '%" + transactionID
                + "%' AND (p.sagaStatus = 'FAILED' OR p.sagaStatus = 'COMPLETED')";
        return cluster.query(qryString, QueryOptions.queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS))
                .rowsAs(TransactionSaga.class);
    }

    @Override
    public void update(String sagaId, TransactionSaga transactionSaga) {
        MutationResult replace = collection.replace(sagaId, transactionSaga);
        log.info("Saga record updated for transaction: {}", replace);
    }

    @Override
    public List<TransactionSaga> findAlreadySendNotifyRecords() {
        String qryString = "SELECT p.* FROM " + dbProperties.getBucketName() + " WHERE p.sagaStatus LIKE '%"
                + SagaStatus.TRANSACTION_COMPLETED.name() + "%' OR p.sagaStatus LIKE '%"
                + SagaStatus.TRANSACTION_FAILED.name() + "%'";
        return cluster.query(qryString, QueryOptions.queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS))
                .rowsAs(TransactionSaga.class);
    }

    private static TransactionSaga getTransactionSaga(Transaction transaction) {
        return TransactionSaga.builder().sagaId(UUID.randomUUID().toString()).transactionID(transaction.getId())
                .accountID(String.valueOf(transaction.getAccountID())).sagaStatus(SagaStatus.STARTED.name()).build();
    }
}

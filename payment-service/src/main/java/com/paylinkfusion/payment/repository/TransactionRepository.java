package com.paylinkfusion.payment.repository;

import com.paylinkfusion.payment.models.Transaction;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findAllByTransactionStatus(String status);
}

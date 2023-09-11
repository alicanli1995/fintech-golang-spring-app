package com.paylinkfusion.payment.repository;

import com.paylinkfusion.payment.models.Wallet;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletRepository extends MongoRepository<Wallet, String> {

    Optional<Wallet> findByAccountID(long accountId);

}

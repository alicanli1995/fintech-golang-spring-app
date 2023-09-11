package com.paylinkfusion.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paylinkfusion.payment.models.TransactionSaga;
import com.paylinkfusion.payment.models.Wallet;
import com.paylinkfusion.payment.models.dto.AmountDTO;
import com.paylinkfusion.payment.models.dto.enums.WalletStatus;
import com.paylinkfusion.payment.models.dto.request.UserRegisterEvent;
import com.paylinkfusion.payment.repository.WalletRepository;
import com.paylinkfusion.payment.service.WalletService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Wallet getWalletByAccountId(long accountId) {
        return walletRepository.findByAccountID(accountId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for account id: " + accountId));
    }

    @Override
    public void handleUserRegister(String userRegisterEvent) {
        try {
            var event = objectMapper.readValue(userRegisterEvent, UserRegisterEvent.class);
            walletRepository.insert(
                    Wallet.builder().accountID(event.accountId()).walletStatus(WalletStatus.ACTIVE.name())
                            .amount(AmountDTO.builder().amount(BigDecimal.ZERO).currency("USD").build()).build());
            log.info("User register event processed: {}", userRegisterEvent);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while processing user register event: {}", userRegisterEvent, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void chargeAmount(TransactionSaga transactionSaga, AmountDTO amount) {
        Wallet wallet = getWalletByAccountId(Long.parseLong(transactionSaga.getAccountID()));
        BigDecimal newAmount = wallet.getAmount().amount().add(amount.amount());
        wallet.setAmount(AmountDTO.builder().amount(newAmount).currency(amount.currency()).build());
        walletRepository.save(wallet);
        log.info("Amount charged for transaction: {}", transactionSaga);
    }

}

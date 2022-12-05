package com.example.account.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDto useBallance(Long userId, String accountNumber, Long amount) {
        AccountUser user = accountUserRepository.findById(userId)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountException(ErrorCode.ACCUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBallance(amount);

        return TransactionDto.fromEntity(transactionRepository.save(
            Transaction.builder()
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.S)
            .account(account)
            .balanceSnapshot(account.getBalance())
            .transactionId(UUID.randomUUID().toString().replace("-", ""))
            .transactedAt(LocalDateTime.now())
            .build()
        ));
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if(!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(ErrorCode.USER_ACCOUNT_NOT_MATCH);
        }
        if(account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERD);
        }
        if(account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    public void saveFailUseTransaction(String accountNumber, @NotNull Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new AccountException(ErrorCode.ACCUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, account, amount);
    }

    private Transaction saveAndGetTransaction(TransactionResultType transactionResultType, Account account, Long amount) {
        return transactionRepository.save(
            Transaction.builder()
            .transactionType(TransactionType.USE)
            .transactionResultType(transactionResultType)
            .account(account)
            .amount(amount)
            .balanceSnapshot(account.getBalance())
            .transactionId(UUID.randomUUID().toString().replace("-", ""))
            .transactedAt(LocalDateTime.now())
            .build()
        );

    }
}

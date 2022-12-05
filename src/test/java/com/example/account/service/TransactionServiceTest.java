package com.example.account.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void successUseBallance() {
        AccountUser pobi = AccountUser.builder()
        .id(1L)
        .name("Pobi").build();
        Account account = Account.builder()
            .accountUser(pobi)
            .accountStatus(AccountStatus.IN_USE)
            .balance(10000L)
            .accountNumber("1000000012")
            .build();
        given(accountUserRepository.findById(anyLong()))
            .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
            .willReturn(Optional.of(account));    
        given(transactionRepository.save(any()))
            .willReturn(Transaction.builder()
            .account(account)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.S)
            .transactionId("transactionId")
            .transactedAt(LocalDateTime.now())
            .amount(1000L)
            .balanceSnapshot(9000L)
            .build());

        TransactionDto transactionDto = transactionService.useBallance(1L, "1000000000", 200L);

        assertEquals(TransactionResultType.S, transactionDto.getTransactionResultType());
        assertEquals(TransactionType.USE, transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnapshot());
        assertEquals(1000L, transactionDto.getAmount());
        


    }
}

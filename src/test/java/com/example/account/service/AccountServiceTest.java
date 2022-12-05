package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        Account account = accountService.getAccount(4555L);

        //then
        verify(accountRepository, times(1)).findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(4555L, captor.getValue());
        assertNotEquals(45515L, captor.getValue());
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 - 음수로 조회")
    void testFailedToSearchAccount() {
        //given
        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-10L));

        //then
        assertEquals("Minus", exception.getMessage());
    }

    @Test
    @DisplayName("Test 이름 변경")
    void testGetAccount() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void testGetAccount2() {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        //when
        Account account = accountService.getAccount(4555L);

        //then
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    void createAccountSuccess() {
        AccountUser user = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
        .willReturn(Optional.of(Account.builder()
        .accountNumber("1000000012").build()));        
        given(accountRepository.save(any()))
        .willReturn(Account.builder()
        .accountUser(user)
        .accountNumber("1000000013").build());
        
    }
    @Test
    void deleteAccountSuccess() {
        AccountUser user = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();

        given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
        .willReturn(Optional.of(Account.builder()
        .accountUser(user)
        .balance(0L)
        .accountNumber("1000000012").build()));        

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }
    @Test
    void successGetAccountByUserId() {
        AccountUser pobi = AccountUser.builder()
        .id(12L)
        .name("Pobi").build();
        List<Account> accounts = Arrays.asList(
            Account.builder()
            .accountUser(pobi)
            .accountNumber("1111111111")
            .balance(1000L)
            .build(),
            Account.builder()
            .accountUser(pobi)
            .accountNumber("2222222222")
            .balance(2000L)
            .build(),
            Account.builder()
            .accountUser(pobi)
            .accountNumber("3333333333")
            .balance(3000L)
            .build()
        );
        given(accountUserRepository.findById(anyLong()))
        .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountUser(any()))
        .willReturn(accounts);

        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        assertEquals(3, accountDtos.size());
        assertEquals("1111111111", accountDtos.get(0).getAccountnumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("2222222222", accountDtos.get(1).getAccountnumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("3333333333", accountDtos.get(2).getAccountnumber());
        assertEquals(3000, accountDtos.get(2).getBalance());

    }
}
package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountDto;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import com.example.account.service.RedisTestService;
import com.example.account.type.AccountStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisTestService redisTestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successCreateAccount() throws Exception {
        given(accountService.createAccount(anyLong(), anyLong()))
        .willReturn(AccountDto.builder()
        .userId(1L)
        .accountnumber("1234567890").
        registeredAt(LocalDateTime.now())
        .unRegisteredAt(LocalDateTime.now()).build());

        mockMvc.perform(post("/account")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(new CreateAccount.Request(3333L, 1111L))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
    }

    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.IN_USE)
                        .build());

        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value("3456"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
    }

    @Test
    void successDeleteAccount() throws Exception {
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                .userId(1L)
                .accountnumber("1234567890")
                .registeredAt(LocalDateTime.now())
                .unRegisteredAt(LocalDateTime.now())
                .build());

        //when
        //then
        mockMvc.perform(delete("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new DeleteAccount.Request(3333L, "1111111111")
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value("1"))
        .andExpect(jsonPath("$.accountNumber").value("1234567890"))
        .andDo(print());
        
    }

    @Test
    void testGetAccountsByUserId() throws Exception {
        List<AccountDto> accountDtos = Arrays.asList(
            AccountDto.builder()
            .accountnumber("1234567890")
            .balance(1999L).build(),
            AccountDto.builder()
            .accountnumber("1000000111")
            .balance(1500L).build(),
            AccountDto.builder()
            .accountnumber("2222222222")
            .balance(3000L).build()
        );
        given(accountService.getAccountsByUserId(anyLong()))
        .willReturn(accountDtos);

        mockMvc.perform(get("/account?user_id=1"))
        .andDo(print())
        .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
        .andExpect(jsonPath("$[0].balance").value(1999L));
    }
}
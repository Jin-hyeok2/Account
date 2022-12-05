package com.example.account.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.dto.UseBallance;
import com.example.account.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    public UseBallance.Response useBallance(
        @Valid @RequestBody UseBallance.Request request
    ) {
        try {
            return UseBallance.Response.from(transactionService.useBallance(
            request.getUserId(), 
            request.getAccountNumber(), 
            request.getAmount()));
        } catch (Exception e) {
            // TODO: handle exception
            log.error("Failed to use balance", e);

            transactionService.saveFailUseTransaction(
                request.getAccountNumber(),
                request.getAmount()
            );
            throw e;
        }
    }
}

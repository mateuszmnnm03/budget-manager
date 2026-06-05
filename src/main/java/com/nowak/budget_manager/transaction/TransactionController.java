package com.nowak.budget_manager.transaction;

import com.nowak.budget_manager.transaction.dto.TransactionRequest;
import com.nowak.budget_manager.transaction.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionResponse> getTransactions(@RequestParam(required = false) LocalDate from,@RequestParam(required = false) LocalDate to,@RequestParam(required = false) String category){
        return transactionService.getTransactions(from, to, category);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody @Valid TransactionRequest req){
           return transactionService.createTransaction(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable  Long id){
        transactionService.deleteTransaction(id);
    }
}

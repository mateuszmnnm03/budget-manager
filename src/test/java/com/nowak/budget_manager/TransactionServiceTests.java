package com.nowak.budget_manager;

import com.nowak.budget_manager.account.Account;
import com.nowak.budget_manager.account.AccountRepository;
import com.nowak.budget_manager.common.exception.ResourceNotFoundException;
import com.nowak.budget_manager.limit.CategoryLimit;
import com.nowak.budget_manager.limit.CategoryLimitRepository;
import com.nowak.budget_manager.transaction.Transaction;
import com.nowak.budget_manager.transaction.TransactionRepository;
import com.nowak.budget_manager.transaction.TransactionService;
import com.nowak.budget_manager.transaction.TransactionType;
import com.nowak.budget_manager.transaction.dto.TransactionRequest;
import com.nowak.budget_manager.transaction.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryLimitRepository categoryLimitRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createIncomeTransactionTest() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.ZERO);

        TransactionRequest req = new TransactionRequest();
        req.setAccountId(1L);
        req.setAmount(new BigDecimal("200.00"));
        req.setType(TransactionType.INCOME);
        req.setCategory("Salary");
        req.setTransactionDate(LocalDate.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TransactionResponse res = transactionService.createTransaction(req);

        assertEquals(new BigDecimal("200.00"), res.getAmount());
        assertEquals(TransactionType.INCOME, res.getType());
        assertEquals("Salary", res.getCategory());
        assertEquals(new BigDecimal("200.00"), account.getBalance());
    }

    @Test
    void createExpenseTransactionTest() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("500.00"));

        TransactionRequest req = new TransactionRequest();
        req.setAccountId(1L);
        req.setAmount(new BigDecimal("100.00"));
        req.setType(TransactionType.EXPENSE);
        req.setCategory("Food");
        req.setTransactionDate(LocalDate.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        transactionService.createTransaction(req);

        assertEquals(new BigDecimal("400.00"), account.getBalance());
    }

    @Test
    void createTransactionAccountNotFoundTest() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        TransactionRequest req = new TransactionRequest();
        req.setAccountId(99L);
        req.setAmount(new BigDecimal("100.00"));
        req.setType(TransactionType.INCOME);
        req.setCategory("Salary");
        req.setTransactionDate(LocalDate.now());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(req));
    }

    @Test
    void deleteTransactionRestoresBalanceTest() {
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("300.00"));

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setAccount(account);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L);

        assertEquals(new BigDecimal("400.00"), account.getBalance());
        verify(transactionRepository).delete(transaction);
        verify(accountRepository).save(account);
    }

    @Test
    void deleteNonExistingTransactionTest() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.deleteTransaction(99L));
    }

    @Test
    void createTransactionExceedingLimit(){
        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("300.00"));

        TransactionRequest req = new TransactionRequest();
        req.setAccountId(1L);
        req.setAmount(new BigDecimal("100.00"));
        req.setType(TransactionType.EXPENSE);
        req.setCategory("Sport");
        req.setTransactionDate(LocalDate.now());

        Transaction existing = new Transaction();
        existing.setAmount(new BigDecimal("100.00"));
        existing.setType(TransactionType.EXPENSE);
        existing.setCategory("Sport");
        existing.setAccount(account);

        when(transactionRepository.findByTypeAndCategory(TransactionType.EXPENSE, "Sport"))
                .thenReturn(List.of(existing));

        CategoryLimit limit = new CategoryLimit();
        limit.setLimitAmount(new BigDecimal("99.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });
        when(categoryLimitRepository.findByAccount_IdAndCategory(1L, "Sport"))
                .thenReturn(Optional.of(limit));
        when(transactionRepository.findByTypeAndCategory(TransactionType.EXPENSE, "Sport"))
                .thenReturn(List.of(existing));

        TransactionResponse res = transactionService.createTransaction(req);

        assertNotNull(res.getWarning());


    }
}
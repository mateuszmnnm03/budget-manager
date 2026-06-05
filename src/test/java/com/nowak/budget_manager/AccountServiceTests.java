package com.nowak.budget_manager;

import com.nowak.budget_manager.account.Account;
import com.nowak.budget_manager.account.AccountRepository;
import com.nowak.budget_manager.account.AccountService;
import com.nowak.budget_manager.account.dto.AccountRequest;
import com.nowak.budget_manager.account.dto.AccountResponse;
import com.nowak.budget_manager.common.exception.AccountHasTransactionsException;
import com.nowak.budget_manager.common.exception.NameConflictException;
import com.nowak.budget_manager.common.exception.ResourceNotFoundException;
import com.nowak.budget_manager.transaction.TransactionRepository;
import com.nowak.budget_manager.transaction.TransactionService;
import com.nowak.budget_manager.transaction.TransactionType;
import com.nowak.budget_manager.transaction.dto.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountTest() {
        AccountRequest req = new AccountRequest();
        req.setName("Account");

        when(accountRepository.existsByName("Account")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AccountResponse res = accountService.createAccount(req);

        assertEquals("Account", res.getName());
        assertEquals(1L, res.getId());
        assertEquals(BigDecimal.ZERO, res.getBalance());
    }

    @Test
    void createSameNameAccounts() {
        when(accountRepository.existsByName("Account")).thenReturn(true);

        AccountRequest req = new AccountRequest();
        req.setName("Account");

        assertThrows(NameConflictException.class, () -> accountService.createAccount(req));
    }

    @Test
    void getAccountTest() {
        Account account = new Account();
        account.setId(1L);
        account.setName("Account");
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse res = accountService.getAccount(1L);

        assertEquals("Account", res.getName());
        assertEquals(1L, res.getId());
        assertEquals(BigDecimal.ZERO, res.getBalance());
    }

    @Test
    void getNonExistingAccountTest() {
        when(accountRepository.findById(12L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccount(12L));
    }

    @Test
    void deleteAccountTest() {
        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.existsByAccount_Id(1L)).thenReturn(false);

        accountService.deleteAccount(1L);

        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccountWithTransactionsTest() {
        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.existsByAccount_Id(1L)).thenReturn(true);

        assertThrows(AccountHasTransactionsException.class, () -> accountService.deleteAccount(1L));
    }
}

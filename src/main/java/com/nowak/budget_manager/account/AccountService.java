package com.nowak.budget_manager.account;

import com.nowak.budget_manager.account.dto.AccountRequest;
import com.nowak.budget_manager.account.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccount(Long id){
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found: " + id));
    }

    public List<Account> getAccountList() {
        return accountRepository.findAll();
    }

    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(request.getBalance());
        Account saved = accountRepository.save(account);
        return new AccountResponse(saved.getId(), saved.getName(), saved.getBalance());
    }

    public void deleteAccount(Long id){
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
    }
}

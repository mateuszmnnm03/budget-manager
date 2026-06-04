package com.nowak.budget_manager.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
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

}

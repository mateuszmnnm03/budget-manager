package com.nowak.budget_manager.account;

import com.nowak.budget_manager.account.dto.AccountRequest;
import com.nowak.budget_manager.account.dto.AccountResponse;
import com.nowak.budget_manager.common.exception.AccountHasTransactionsException;
import com.nowak.budget_manager.common.exception.NameConflictException;
import com.nowak.budget_manager.common.exception.ResourceNotFoundException;
import com.nowak.budget_manager.transaction.Transaction;
import com.nowak.budget_manager.transaction.TransactionRepository;
import com.nowak.budget_manager.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountResponse getAccount(Long id){
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
        return new AccountResponse(account.getId(), account.getName(), account.getBalance());
    }

    public List<AccountResponse> getAccountList() {
        return accountRepository.findAll().stream().map(a -> new AccountResponse(a.getId(), a.getName(), a.getBalance())).toList();
    }

    public AccountResponse createAccount(AccountRequest request) {
        if(accountRepository.existsByName(request.getName())){
            throw new NameConflictException("Account " + request.getName() + " already exists.");
        }
        Account account = new Account();
        account.setName(request.getName());
        account.setBalance(request.getBalance());
        Account saved = accountRepository.save(account);
        return new AccountResponse(saved.getId(), saved.getName(), saved.getBalance());
    }

    public void deleteAccount(Long id){
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account " + id + " not found."));
        if(transactionRepository.existsByAccount_Id(id)){
            throw new AccountHasTransactionsException("Account " + id + " has transactions.");
        }
        accountRepository.delete(account);
    }

    public byte[] exportTransactionsToCsv(Long id) {
        accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account " + id + " not found."));

        List<Transaction> transactions = transactionRepository.findByAccount_Id(id);

        StringBuilder sb = new StringBuilder();
        sb.append("id,amount,type,category,description,date\n");
        for (Transaction t : transactions) {
            sb.append(t.getId()).append(",")
                    .append(t.getAmount()).append(",")
                    .append(t.getType()).append(",")
                    .append(t.getCategory()).append(",")
                    .append(t.getDescription() != null ? t.getDescription() : "").append(",")
                    .append(t.getTransactionDate()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}

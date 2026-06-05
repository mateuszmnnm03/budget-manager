package com.nowak.budget_manager.transaction;

import com.nowak.budget_manager.account.Account;
import com.nowak.budget_manager.account.AccountRepository;
import com.nowak.budget_manager.common.exception.ResourceNotFoundException;
import com.nowak.budget_manager.transaction.dto.TransactionRequest;
import com.nowak.budget_manager.transaction.dto.TransactionResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionsRepository;
    private final AccountRepository accountRepository;

    public List<TransactionResponse> getTransactions(LocalDate from, LocalDate to, String category){
        Specification<Transaction> spec = Specification.where(null);

        if (from != null){
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("transactionDate"), from));
        }

        if (to != null){
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("transactionDate"), to));
        }

        if(category != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }

        return transactionsRepository.findAll(spec).stream().map(t -> new TransactionResponse(t.getId(), t.getAmount(), t.getType(), t.getCategory(), t.getDescription(), t.getTransactionDate(), t.getAccount().getId())).toList();
    }

    public TransactionResponse createTransaction(TransactionRequest req){
        Account account = accountRepository.findById(req.getAccountId()).orElseThrow(() -> new ResourceNotFoundException("Account " + req.getAccountId() + " not found." ));
        Transaction transaction = new Transaction();
        transaction.setAmount(req.getAmount());
        transaction.setCategory(req.getCategory());
        transaction.setType(req.getType());
        transaction.setTransactionDate(req.getTransactionDate());
        if(req.getDescription() != null){
            transaction.setDescription(req.getDescription());
        }

        BigDecimal diff = req.getType() == TransactionType.EXPENSE ? req.getAmount().negate() : req.getAmount();
        account.setBalance(account.getBalance().add(diff));

        transaction.setAccount(account);
        accountRepository.save(account);
        Transaction saved = transactionsRepository.save(transaction);
        return new TransactionResponse(transaction.getId(), transaction.getAmount(), transaction.getType(), transaction.getCategory(), transaction.getDescription(), transaction.getTransactionDate(), transaction.getAccount().getId());
    }

    public void deleteTransaction(Long id){
        Transaction transaction = transactionsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction " + id + " not found."));
        Account account = transaction.getAccount();

        BigDecimal amount = transaction.getAmount();
        BigDecimal diff = transaction.getType() == TransactionType.EXPENSE ? amount.negate() : amount;

        account.setBalance(account.getBalance().subtract(diff));
        accountRepository.save(account);
        transactionsRepository.delete(transaction);
    }

}

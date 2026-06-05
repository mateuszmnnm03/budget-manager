package com.nowak.budget_manager.summary;

import com.nowak.budget_manager.transaction.Transaction;
import com.nowak.budget_manager.transaction.TransactionRepository;
import com.nowak.budget_manager.transaction.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SummaryService {
    
    private final TransactionRepository transactionRepository;


    public SummaryResponse getSummary() {
        BigDecimal income = getIncomes();
        BigDecimal expenses = getExpenses();

        Map<String, BigDecimal> expensesMap = transactionRepository.findByType(TransactionType.EXPENSE).stream().collect(Collectors.groupingBy(
                Transaction::getCategory, Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
        ));
        return new SummaryResponse(income, expenses, expensesMap);
    }

    public BigDecimal getIncomes() {
        return transactionRepository.findByType(TransactionType.INCOME).stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getExpenses() {
        return transactionRepository.findByType(TransactionType.EXPENSE).stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getExpensesByCategory(String category) {
        return transactionRepository.findByTransactionDateBetweenAndCategory(null,null,category).stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

package com.nowak.budget_manager.transaction;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    boolean existsByAccount_Id(Long accountId);

    List<Transaction> findByTransactionDateBetweenAndCategory(LocalDate from, LocalDate to, String category);
    List<Transaction> findByType(TransactionType type);
}

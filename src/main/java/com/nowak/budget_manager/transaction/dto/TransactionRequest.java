package com.nowak.budget_manager.transaction.dto;

import com.nowak.budget_manager.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Transaction amount is required.")
    @Positive(message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required.")
    private TransactionType type;

    @NotBlank(message = "Category is required.")
    private String category;

    private String description;

    @NotNull(message = "Transaction date is required.")
    private LocalDate transactionDate = LocalDate.now();

    @NotNull(message = "Account id is required.")
    private Long accountId;
}

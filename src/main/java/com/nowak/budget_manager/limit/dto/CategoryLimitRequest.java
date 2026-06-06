package com.nowak.budget_manager.limit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryLimitRequest {
    @NotBlank(message = "Category is required.")
    private String category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be grater than 0.")
    private BigDecimal limitAmount;
}

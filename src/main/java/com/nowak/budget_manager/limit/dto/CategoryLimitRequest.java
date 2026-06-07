package com.nowak.budget_manager.limit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoryLimitRequest {
    @NotBlank(message = "Category is required.")
    @Size(max=100, message = "Category name too long.")
    private String category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be grater than 0.")
    private BigDecimal limitAmount;
}

package com.nowak.budget_manager.limit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryLimitResponse {
    private Long id;
    private String category;
    private BigDecimal limitAmount;
    private Long accountId;
}
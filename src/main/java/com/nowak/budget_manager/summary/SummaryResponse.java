package com.nowak.budget_manager.summary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@Data
public class SummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private Map<String, BigDecimal> expensesByCategory;
}

package com.nowak.budget_manager.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String name;
    private BigDecimal balance;
}

package com.nowak.budget_manager.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {
    @NotBlank(message = "Podaj nazwę konta.")
    @Size(max = 50)
    private String name;

    private BigDecimal balance = BigDecimal.ZERO;
}

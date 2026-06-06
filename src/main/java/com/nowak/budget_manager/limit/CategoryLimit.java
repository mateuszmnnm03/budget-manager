package com.nowak.budget_manager.limit;

import com.nowak.budget_manager.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "category_limits")
public class CategoryLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 15, scale = 2)
    @Positive
    private BigDecimal limitAmount;

    @JoinColumn(nullable = false, name = "account_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;


}

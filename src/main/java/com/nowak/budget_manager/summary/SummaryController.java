package com.nowak.budget_manager.summary;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summary")
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping
    public SummaryResponse getSummary(){
        return summaryService.getSummary();
    }

    @GetMapping("/incomes")
    public BigDecimal getIncomes(){
        return summaryService.getIncomes();
    }

    @GetMapping("/expenses")
    public BigDecimal getExpenses(){
        return summaryService.getExpenses();
    }

    @GetMapping("/expenses/{category}")
    public BigDecimal geExpensesByCategory(@PathVariable String category){
        return summaryService.getExpensesByCategory(category);
    }
}

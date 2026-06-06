package com.nowak.budget_manager.limit;

import com.nowak.budget_manager.limit.dto.CategoryLimitRequest;
import com.nowak.budget_manager.limit.dto.CategoryLimitResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/accounts/{accountId}/limits")
@AllArgsConstructor
public class CategoryLimitController {

    private final CategoryLimitService categoryLimitService;

    @GetMapping
    public List<CategoryLimitResponse> getLimits(@PathVariable Long accountId){
        return categoryLimitService.getLimits(accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryLimitResponse setLimit(@PathVariable Long accountId, @RequestBody @Valid CategoryLimitRequest request) {
        return categoryLimitService.setLimit(accountId, request);
    }

    @DeleteMapping("/{category}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLimit(@PathVariable Long accountId, @PathVariable String category) {
        categoryLimitService.deleteLimit(accountId, category);
    }

}

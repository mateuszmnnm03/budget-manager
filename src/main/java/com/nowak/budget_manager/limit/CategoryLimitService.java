package com.nowak.budget_manager.limit;

import com.nowak.budget_manager.account.Account;
import com.nowak.budget_manager.account.AccountRepository;
import com.nowak.budget_manager.common.exception.NameConflictException;
import com.nowak.budget_manager.common.exception.ResourceNotFoundException;
import com.nowak.budget_manager.limit.dto.CategoryLimitRequest;
import com.nowak.budget_manager.limit.dto.CategoryLimitResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryLimitService {

    private final CategoryLimitRepository categoryLimitRepository;
    private final AccountRepository accountRepository;

    public List<CategoryLimitResponse> getLimits(Long accountId) {
        accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account " + accountId + " not found."));

        return categoryLimitRepository.findByAccount_Id(accountId).stream()
                .map(l -> new CategoryLimitResponse(l.getId(), l.getCategory(), l.getLimitAmount(), l.getAccount().getId())).toList();
    }

    public CategoryLimitResponse setLimit(Long accountId, CategoryLimitRequest request) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account " + accountId + " not found."));

        if (categoryLimitRepository.existsByAccount_IdAndCategory(accountId, request.getCategory())) {
            throw new NameConflictException("Limit for category '" + request.getCategory() + "' already exists.");
        }

        CategoryLimit limit = new CategoryLimit();
        limit.setAccount(account);
        limit.setCategory(request.getCategory());
        limit.setLimitAmount(request.getLimitAmount());

        CategoryLimit saved = categoryLimitRepository.save(limit);
        return new CategoryLimitResponse(saved.getId(), saved.getCategory(), saved.getLimitAmount(), saved.getAccount().getId());
    }

    public void deleteLimit(Long accountId, String category) {
        CategoryLimit limit = categoryLimitRepository.findByAccount_IdAndCategory(accountId, category)
                .orElseThrow(() -> new ResourceNotFoundException("Limit for category '" + category + "' not found."));
        categoryLimitRepository.delete(limit);
    }
}

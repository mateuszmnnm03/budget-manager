package com.nowak.budget_manager.limit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryLimitRepository extends JpaRepository<CategoryLimit, Long> {
    List<CategoryLimit> findByAccount_Id(Long accountId);
    Optional<CategoryLimit> findByAccount_IdAndCategory(Long accountId, String category);
    boolean existsByAccount_IdAndCategory(Long accountId, String category);
}

package com.planit.strategy.domain.trend.repository;

import com.planit.strategy.domain.trend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedAtIsNull();
}

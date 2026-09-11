package com.Robin.core_api.Repository;

import com.Robin.core_api.Model.Entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeReviewRepo extends JpaRepository<CodeReview, Long> {
    List<CodeReview> findAllByUserId(Long userId);
}

package com.Robin.ai_worker.Repository;

import com.Robin.ai_worker.Model.Entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeReviewRepo extends JpaRepository<CodeReview, Long> {

}

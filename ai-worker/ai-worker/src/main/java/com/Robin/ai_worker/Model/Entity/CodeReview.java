package com.Robin.ai_worker.Model.Entity;

import com.Robin.ai_worker.Model.Enums.Language;
import com.Robin.ai_worker.Model.Enums.ReviewStatus;
import com.Robin.ai_worker.Model.Enums.ReviewType;
import com.Robin.ai_worker.Model.Enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(columnDefinition = "TEXT")
    private String codeContent;
    private String codeHash;
    private Integer overallScore;
    @Column(columnDefinition = "TEXT")
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String bugs;
    @Column(columnDefinition = "TEXT")
    private String securityIssues;
    @Column(columnDefinition = "TEXT")
    private String performanceIssues;
    @Column(columnDefinition = "TEXT")
    private String bestPracticeViolations;
    @Column(columnDefinition = "TEXT")
    private String suggestions;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

}

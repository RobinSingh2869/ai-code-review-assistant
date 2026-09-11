package com.Robin.core_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiReviewResponse {
    private Integer overallScore;
    private String summary;
    private String severity;
    private List<String> bugs;
    private List<String> securityIssues;
    private List<String> performanceIssues;
    private List<String> bestPracticeViolations;
    private List<String> suggestions;

}

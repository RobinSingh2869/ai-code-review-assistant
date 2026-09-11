package com.Robin.core_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private int totalReviews;
    private double averageScore;
    private double scoreTrend;
    private String mostCommonIssueType;
    private Map<String, Long> breakdownByLanguage;
    private int bestSubmissionScore;
    private int worstSubmissionScore;
}

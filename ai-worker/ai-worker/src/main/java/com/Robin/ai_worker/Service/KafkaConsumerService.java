package com.Robin.ai_worker.Service;

import com.Robin.ai_worker.DTO.AiReviewResponse;
import com.Robin.ai_worker.DTO.CodeReviewRequest;
import com.Robin.ai_worker.Model.Entity.CodeReview;
import com.Robin.ai_worker.Model.Enums.ReviewStatus;
import com.Robin.ai_worker.Model.Enums.Severity;
import com.Robin.ai_worker.Repository.CodeReviewRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    @Autowired
    private AiReviewService aiReviewService;
    @Autowired
    private CodeReviewRepo repo;
    @Autowired
    private ObjectMapper objectMapper;
    @KafkaListener(topics = "code-review-requests", groupId = "ai-worker-group")
    public void consumeReviewRequest(CodeReviewRequest request){
        log.info("Received Kafka Message from for Review Id: {}", request.getReviewId());
        try {
            AiReviewResponse aiResponse = aiReviewService.analyzeCode(
                    request.getCodeContent(),
                    request.getLanguage(),
                    request.getReviewType()
            );
            CodeReview codeReview = repo.findById(request.getReviewId())
                    .orElseThrow(() -> new RuntimeException("CodeReview Entity not found for Id:" + request.getReviewId()));
            //Mapping
            codeReview.setBugs(objectMapper.writeValueAsString(aiResponse.getBugs()));
            codeReview.setSecurityIssues(objectMapper.writeValueAsString(aiResponse.getSecurityIssues()));
            codeReview.setPerformanceIssues(objectMapper.writeValueAsString(aiResponse.getPerformanceIssues()));
            codeReview.setBestPracticeViolations(objectMapper.writeValueAsString(aiResponse.getBestPracticeViolations()));
            codeReview.setSuggestions(objectMapper.writeValueAsString(aiResponse.getSuggestions()));

            codeReview.setOverallScore(aiResponse.getOverallScore());
            codeReview.setSeverity(Severity.valueOf(aiResponse.getSeverity().toUpperCase()));
            codeReview.setSummary(aiResponse.getSummary());

            codeReview.setStatus(ReviewStatus.COMPLETED);
            codeReview.setCompletedAt(LocalDateTime.now());

            repo.save(codeReview);
            log.info("Successfully completed and saved review for Id: {}", request.getReviewId());
        } catch (Exception e) {
            log.error("Failed to process code review for Id:{}" ,request.getReviewId(),e);
        }
    }
}

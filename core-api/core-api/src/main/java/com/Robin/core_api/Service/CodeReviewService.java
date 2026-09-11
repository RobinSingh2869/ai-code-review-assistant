package com.Robin.core_api.Service;


import com.Robin.core_api.DTO.CodeReviewRequest;
import com.Robin.core_api.DTO.DashboardResponse;
import com.Robin.core_api.DTO.SubmitCodeRequest;
import com.Robin.core_api.Exception.CodeTooLargeException;
import com.Robin.core_api.Exception.RateLimitExceededException;
import com.Robin.core_api.Exception.ResourceNotFoundException;
import com.Robin.core_api.Model.Entity.CodeReview;
import com.Robin.core_api.Model.Entity.User;
import com.Robin.core_api.Model.Enums.ReviewStatus;
import com.Robin.core_api.Repository.CodeReviewRepo;
import com.Robin.core_api.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodeReviewService {
    @Autowired
    private CodeReviewRepo codeReviewRepo;
    @Autowired
    private UserRepo userRepo;
//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public User getAuthenticatedUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not Found with email: "+ email));
    }

    public CodeReview submitCode(SubmitCodeRequest request){

        User user = getAuthenticatedUser();
        //RATE LIMIT CHECK
        if(redisService.isRateLimited(user.getId())){
            throw new RateLimitExceededException("Rate Limited Exceeded. Max 10 Requests per hour");
        }
        if(request.getCodeContent().length() > 5000){
            throw new CodeTooLargeException("Code exceeds maximum allowed size of 5000 characters.");
        }

        CodeReview codeReview = new CodeReview();
        codeReview.setCodeContent(request.getCodeContent());
        codeReview.setReviewType(request.getReviewType());
        codeReview.setLanguage(request.getLanguage());

        String hashcode = DigestUtils.sha256Hex(request.getCodeContent());
        codeReview.setCodeHash(hashcode);
        //TO CHECK IF CODE REVIEWED BEFORE , IF YES THEN RETURN STORED RESULT
        CodeReview cached = redisService.getCachedReview(codeReview.getCodeHash());
        if(cached!=null){
            return cached;
        }
        codeReview.setStatus(ReviewStatus.PENDING);
        codeReview.setSubmittedAt(LocalDateTime.now());
        codeReview.setUser(user);

//        AiReviewResponse aiReviewResponse = restTemplate.postForObject(
//                "http://localhost:8081/internal/analyze",
//                request,
//                AiReviewResponse.class
//        );

//        if(aiReviewResponse !=null){
//            ObjectMapper mapper = new ObjectMapper();
//            try{
//                codeReview.setBugs(mapper.writeValueAsString(aiReviewResponse.getBugs()));
//                codeReview.setSecurityIssues(mapper.writeValueAsString(aiReviewResponse.getSecurityIssues()));
//                codeReview.setPerformanceIssues(mapper.writeValueAsString(aiReviewResponse.getPerformanceIssues()));
//                codeReview.setBestPracticeViolations(mapper.writeValueAsString(aiReviewResponse.getBestPracticeViolations()));
//                codeReview.setSuggestions(mapper.writeValueAsString(aiReviewResponse.getSuggestions()));
//                codeReview.setOverallScore(aiReviewResponse.getOverallScore());
//                codeReview.setSeverity(Severity.valueOf(aiReviewResponse.getSeverity()));
//                codeReview.setSummary(aiReviewResponse.getSummary());
//                codeReview.setStatus(ReviewStatus.COMPLETED);
//                codeReview.setCompletedAt(LocalDateTime.now());
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to parse AI Response lists to JSON", e);
//            }
//        }
        CodeReview saved = codeReviewRepo.save(codeReview);
        redisService.cacheReview(saved.getCodeHash(),saved);
        CodeReviewRequest kafkaMessage = new CodeReviewRequest(
                saved.getId(),
                saved.getCodeContent(),
                saved.getLanguage().name(),
                saved.getReviewType().name()
        );
        kafkaProducerService.sendReviewRequest(kafkaMessage);
        return saved;
    }

    public CodeReview getReviewById(Long userId) {
        return codeReviewRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("Review not Found with Id:" + userId));
    }

    public List<CodeReview> getReviewHistory() {
        User user = getAuthenticatedUser();
//        userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with Id:" + userId));
        return codeReviewRepo.findAllByUserId(user.getId());
    }
    public DashboardResponse getDashboard(){
        User user = getAuthenticatedUser();
        List<CodeReview> reviews = codeReviewRepo.findAllByUserId(user.getId());
        int totalReviews = reviews.size();

        double averageScore = reviews.stream()
                .filter(r -> r.getOverallScore()!=null)
                .mapToInt(CodeReview::getOverallScore)
                .average()
                .orElse(0.0);

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        double scoreTrend = reviews.stream()
                .filter(r->r.getOverallScore()!=null
                && r.getCompletedAt()!=null
                && r.getCompletedAt().isAfter(thirtyDaysAgo))
                .mapToInt(CodeReview::getOverallScore)
                .average()
                .orElse(0.0);

        Map<String, Long> breakdownByLanguage = reviews.stream()
                .collect(Collectors.groupingBy(
                        r->r.getLanguage().name(),
                        Collectors.counting()
                ));

        int bestScore = reviews.stream()
                .filter(r->r.getOverallScore()!=null)
                .mapToInt(CodeReview::getOverallScore)
                .max()
                .orElse(0);

        int worstScore = reviews.stream()
                .filter(r->r.getOverallScore()!=null)
                .mapToInt(CodeReview::getOverallScore)
                .min()
                .orElse(0);

        long bugsCount = reviews.stream()
                .filter(r -> r.getBugs() != null && !r.getBugs().equals("[]"))
                .count();
        long securityCount = reviews.stream()
                .filter(r -> r.getSecurityIssues() != null && !r.getSecurityIssues().equals("[]"))
                .count();
        long performanceIssues = reviews.stream()
                .filter(r -> r.getPerformanceIssues() != null && !r.getPerformanceIssues().equals("[]"))
                .count();
        long bestPracticesCount = reviews.stream()
                .filter(r -> r.getBestPracticeViolations() != null && !r.getBestPracticeViolations().equals("[]"))
                .count();

        Map<String, Long> issueCounts = Map.of(
                "bugs", bugsCount,
                "securityIssues", securityCount,
                "performanceCount", performanceIssues,
                "bestPracticeViolations", bestPracticesCount
        );
        String mostCommonIssueType = issueCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        return new DashboardResponse(
                totalReviews,
                averageScore,
                scoreTrend,
                mostCommonIssueType,
                breakdownByLanguage,
                bestScore,
                worstScore
        );
    }
}

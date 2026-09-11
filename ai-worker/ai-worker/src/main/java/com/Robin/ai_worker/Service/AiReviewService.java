package com.Robin.ai_worker.Service;

import com.Robin.ai_worker.DTO.AiReviewResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientBuilderCustomizer;
import org.springframework.stereotype.Service;

@Service
public class AiReviewService {
    private ChatClient chatClient;

    public AiReviewService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AiReviewResponse analyzeCode(String code, String language, String reviewType){
        String prompt = """
                You are an expert code reviewer. Analyze the following %s code.
                Review Type: %s
                Code: %s
                
                Respond with JSON Containing exactly these fields:
                - bugs: array of strings (empty array if none)
                - securityIssues: array of strings (empty array if none)
                - performanceIssues: array of strings (empty array if none)
                - bestPracticeViolations: array of strings (empty array if none)
                - suggestions: array of strings
                - overallScore: Integer 0-100
                - severity: one of LOW,MEDIUM,HIGH,CRITICAL
                - summary: one paragraph plain English overview
                """.formatted(language,reviewType,code);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiReviewResponse.class);
    }
}

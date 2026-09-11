package com.Robin.core_api.Service;

import com.Robin.core_api.DTO.CodeReviewRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService{
    @Autowired
    private KafkaTemplate<String, CodeReviewRequest> kafkaTemplate;
    private static final String TOPIC = "code-review-requests";
    public void sendReviewRequest(CodeReviewRequest request){
        kafkaTemplate.send(TOPIC,request);
        log.info("Successfully published code review request to topic: {} for request:{}", TOPIC, request);
    }
}

package com.Robin.ai_worker.Controller;

import com.Robin.ai_worker.DTO.AiReviewResponse;
import com.Robin.ai_worker.DTO.SubmitCodeRequest;
import com.Robin.ai_worker.Service.AiReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class AiReviewController {
    @Autowired
    private AiReviewService aiReviewService;

    @PostMapping("/analyze")
    public ResponseEntity<AiReviewResponse> analyze(@RequestBody SubmitCodeRequest request){
        // We call toString() on the enums to pass them as Strings to the service layer
        AiReviewResponse aiReviewResponse = aiReviewService.analyzeCode(
                request.getCodeContent(),
                request.getLanguage().toString(),
                request.getReviewType().toString()
        );
        return new ResponseEntity<>(aiReviewResponse, HttpStatus.ACCEPTED);
    }
}

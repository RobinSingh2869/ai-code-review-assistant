package com.Robin.core_api.Controller;

import com.Robin.core_api.DTO.DashboardResponse;
import com.Robin.core_api.DTO.SubmitCodeRequest;
import com.Robin.core_api.Model.Entity.CodeReview;
import com.Robin.core_api.Service.CodeReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class CodeReviewController {
    @Autowired
    private CodeReviewService codeReviewService;

    @PostMapping("/submit")
    public ResponseEntity<CodeReview> submit( @Valid @RequestBody SubmitCodeRequest request){
        CodeReview codeReview = codeReviewService.submitCode(request);
        return new ResponseEntity<>(codeReview, HttpStatus.ACCEPTED);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<CodeReview> fetchReviewById(@PathVariable Long userId){
        CodeReview codeReview = codeReviewService.getReviewById(userId);
        return new ResponseEntity<>(codeReview,HttpStatus.OK);
    }
    @GetMapping("/history")
    public ResponseEntity<List<CodeReview>> fetchReviewHistory(){
        List<CodeReview> history = codeReviewService.getReviewHistory();
        return new ResponseEntity<>(history, HttpStatus.ACCEPTED);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(){
        return new ResponseEntity<>(codeReviewService.getDashboard(), HttpStatus.OK);
    }
}

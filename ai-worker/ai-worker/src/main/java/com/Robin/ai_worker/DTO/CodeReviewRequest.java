package com.Robin.ai_worker.DTO;


import com.Robin.ai_worker.Model.Enums.Language;
import com.Robin.ai_worker.Model.Enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewRequest {
    private Long reviewId;
    private String codeContent;
    private String language;
    private String reviewType;
}

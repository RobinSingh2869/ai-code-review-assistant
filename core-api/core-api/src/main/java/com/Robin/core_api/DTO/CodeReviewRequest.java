package com.Robin.core_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CodeReviewRequest{
    private Long reviewId;
    private String codeContent;
    private String language;
    private String reviewType;
}


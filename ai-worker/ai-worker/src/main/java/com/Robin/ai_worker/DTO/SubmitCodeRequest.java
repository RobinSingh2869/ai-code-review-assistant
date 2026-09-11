package com.Robin.ai_worker.DTO;


import com.Robin.ai_worker.Model.Enums.ReviewType;
import com.Robin.ai_worker.Model.Enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitCodeRequest {
    private String codeContent;
    private Language language;
    private ReviewType reviewType;
}

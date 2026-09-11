package com.Robin.core_api.DTO;


import com.Robin.core_api.Model.Enums.Language;
import com.Robin.core_api.Model.Enums.ReviewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitCodeRequest {
    private Long reviewId;
    @NotBlank(message = "Code Content cannot be Empty ")
    private String codeContent;
    @NotNull(message = "Language is Required")
    private Language language;
    @NotNull(message = "Review Type is Required")
    private ReviewType reviewType;
}

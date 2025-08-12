package com.audio.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request object for creating a new comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    @Schema(description = "Content of the comment (2000 characters max)", required = true, example = "This product is amazing!")
    @NotBlank
    @Size(max = 2000)
    private String content;
}
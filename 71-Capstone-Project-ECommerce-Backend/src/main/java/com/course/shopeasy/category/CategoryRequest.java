package com.course.shopeasy.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body of POST /api/v1/categories and PUT /api/v1/categories/{id} (ADMIN only). */
public record CategoryRequest(
        @Schema(example = "Sports")
        @NotBlank @Size(max = 80)
        String name,

        @Schema(example = "Sports and fitness equipment")
        @Size(max = 255)
        String description) {
}

package com.course.apiclient.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/*
 * Body we SEND when creating a post (no id - the server gives it).
 */
public record NewPost(
        @NotNull @Positive Long userId,
        @NotBlank String title,
        @NotBlank String body) {
}

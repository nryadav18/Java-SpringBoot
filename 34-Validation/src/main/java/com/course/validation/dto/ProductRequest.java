package com.course.validation.dto;

import com.course.validation.validation.ValidationGroups.OnCreate;
import com.course.validation.validation.ValidationGroups.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/*
 * ProductRequest - ONE DTO used for both create and update, with VALIDATION GROUPS.
 * ---------------------------------------------------------------------------------
 *   - When CREATING, the client must NOT send an id      -> @Null(groups = OnCreate.class)
 *   - When UPDATING, the client MUST send the id          -> @NotNull(groups = OnUpdate.class)
 *   - name and price have no "groups" -> they belong to the Default group.
 *     Because OnCreate and OnUpdate EXTEND Default, these rules run in both cases.
 *
 * The controller picks the group with @Validated(OnCreate.class) or @Validated(OnUpdate.class).
 *
 * (Many teams simply use two separate DTOs instead - often clearer. Groups are shown
 *  here because you will meet them in existing projects.)
 */
public record ProductRequest(

        @Null(groups = OnCreate.class, message = "Id must NOT be sent when creating a product")
        @NotNull(groups = OnUpdate.class, message = "Id is required when updating a product")
        Long id,

        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price
) {
}

package com.course.validation.validation;

import jakarta.validation.groups.Default;

/*
 * Validation groups are just EMPTY "marker" interfaces used as labels.
 *
 * Why "extends Default"? Constraints WITHOUT a groups attribute belong to the
 * Default group. If OnCreate did not extend Default, then
 * @Validated(OnCreate.class) would ONLY run the OnCreate rules and skip
 * @NotBlank on name, @Positive on price, ... - a classic surprise!
 */
public final class ValidationGroups {

    private ValidationGroups() {
        // only holds the nested interfaces
    }

    public interface OnCreate extends Default {
    }

    public interface OnUpdate extends Default {
    }
}

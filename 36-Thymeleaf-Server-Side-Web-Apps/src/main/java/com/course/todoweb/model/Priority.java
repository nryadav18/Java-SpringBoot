package com.course.todoweb.model;

/*
 * Priority of a todo. An enum (topic 15) with a friendly label for the UI.
 * The form shows a <select> with one <option> per value.
 */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;   // used in templates as ${p.label}
    }
}

package com.course.files.storage;

/*
 * Information about a stored file - returned to the client as JSON.
 *
 *   storedName   - the SAFE name on disk, e.g. "3f2a9c1b7d4e_holiday_photo.png" (unique, sanitized)
 *   originalName - the name the user's computer sent (only for display, never used as a path)
 */
public record StoredFile(String storedName, String originalName, long size, String contentType,
                         String downloadUrl, String viewUrl) {
}

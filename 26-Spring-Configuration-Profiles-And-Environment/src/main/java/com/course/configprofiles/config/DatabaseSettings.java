/*
 * A simple record describing which database the app talks to.
 * (We don't connect to a real database in this topic - we only show WHICH settings
 * each profile would use.)
 */
package com.course.configprofiles.config;

public record DatabaseSettings(String url, String username, int poolSize) {
}

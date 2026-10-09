/*
 * A tiny settings bean injected into DatabaseConnectionPool by SETTER injection,
 * so we can see the "properties are set" step of the lifecycle.
 * It is a record (Topic 15) registered with @Bean in AppConfig.
 */
package com.course.scopes.lifecycle;

public record PoolSettings(int size) {
}

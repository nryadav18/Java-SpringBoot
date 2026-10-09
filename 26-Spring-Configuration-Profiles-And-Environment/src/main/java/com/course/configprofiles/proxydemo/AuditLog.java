/*
 * Helper for the proxyBeanMethods demo. Every new AuditLog gets a new number,
 * so we can SEE whether two services share one AuditLog or got separate ones.
 */
package com.course.configprofiles.proxydemo;

public class AuditLog {

    private static int created = 0;
    private final int number;

    public AuditLog() {
        number = ++created;
    }

    public int number() {
        return number;
    }
}

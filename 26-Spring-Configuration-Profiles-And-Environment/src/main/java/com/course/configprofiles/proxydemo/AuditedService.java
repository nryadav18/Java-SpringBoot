/*
 * A service that needs an AuditLog. Used twice in each demo config (orders + payments).
 */
package com.course.configprofiles.proxydemo;

public class AuditedService {

    private final String name;
    private final AuditLog auditLog;

    public AuditedService(String name, AuditLog auditLog) {
        this.name = name;
        this.auditLog = auditLog;
    }

    public String describe() {
        return name + " uses AuditLog #" + auditLog.number();
    }

    public AuditLog auditLog() {
        return auditLog;
    }
}

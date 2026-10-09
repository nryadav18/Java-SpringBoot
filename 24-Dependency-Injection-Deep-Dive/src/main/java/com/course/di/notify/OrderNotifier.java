/*
 * OPTIONAL DEPENDENCIES - three ways to say "inject it IF it exists".
 *
 *  1. Optional<SmsGateway> in the constructor   -> Optional.empty() when there is no bean
 *  2. ObjectProvider<SmsGateway>                 -> ask later: getIfAvailable() returns null if none
 *  3. @Autowired(required = false) on a setter   -> Spring simply doesn't call the setter
 *
 * Without one of these, a missing bean makes startup FAIL with
 * "No qualifying bean of type 'SmsGateway' available".
 */
package com.course.di.notify;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderNotifier {

    private final Optional<SmsGateway> smsGateway;            // way 1
    private final ObjectProvider<SmsGateway> smsProvider;     // way 2
    private SmsGateway backupSms;                             // way 3 (stays null)

    public OrderNotifier(Optional<SmsGateway> smsGateway, ObjectProvider<SmsGateway> smsProvider) {
        this.smsGateway = smsGateway;
        this.smsProvider = smsProvider;
    }

    @Autowired(required = false)
    public void setBackupSms(SmsGateway backupSms) {
        // Never called in this project, because no SmsGateway bean exists.
        this.backupSms = backupSms;
    }

    public String notifyCustomer(String phone, String text) {
        if (smsGateway.isPresent()) {
            smsGateway.get().sendSms(phone, text);
            return "SMS sent";
        }
        return "No SMS gateway installed -> skipped SMS (Optional empty: " + smsGateway.isEmpty()
                + ", provider gives: " + smsProvider.getIfAvailable()
                + ", setter-injected: " + backupSms + ")";
    }
}

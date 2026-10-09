/*
 * An OPTIONAL dependency. On purpose, there is NO bean implementing this interface
 * in the project (imagine the SMS module is only installed for some customers).
 * OrderNotifier shows how to depend on something that may not exist.
 */
package com.course.di.notify;

public interface SmsGateway {
    void sendSms(String phone, String text);
}

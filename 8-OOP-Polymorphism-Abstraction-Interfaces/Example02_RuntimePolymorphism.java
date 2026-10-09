/*
 * Example02_RuntimePolymorphism.java
 * ------------------------------------------------------------
 * What this file shows (domain: an app sending notifications):
 *   RUNTIME polymorphism (dynamic polymorphism / dynamic method dispatch):
 *   a PARENT-type variable can hold ANY CHILD object, and the overridden method
 *   of the REAL object runs. The decision is made while the program runs.
 *
 *   1. Notification n = new EmailNotification(...);  -> n.send() runs the EMAIL version.
 *   2. One loop handles every kind of notification - no if/else on the type.
 *   3. A method that accepts the parent type works with all children (and future ones!).
 *
 * How to run (from this folder):
 *   java Example02_RuntimePolymorphism.java
 */
public class Example02_RuntimePolymorphism {

    public static void main(String[] args) {

        // Parent-type variables, child objects (this is called UPCASTING - automatic and safe)
        Notification n1 = new EmailNotification("priya@shop.com");
        Notification n2 = new SmsNotification("98480-12345");
        Notification n3 = new PushNotification("device-42");

        // Same method call, three different behaviours:
        n1.send("Your order has shipped");
        n2.send("Your order has shipped");
        n3.send("Your order has shipped");

        // The real power: treat them all the same way.
        System.out.println();
        Notification[] channels = { n1, n2, n3, new Notification("generic") };
        for (Notification channel : channels) {
            channel.send("Sale starts tonight!");
        }

        // A method written for the PARENT works for every child:
        System.out.println();
        alertCustomer(new SmsNotification("90000-11111"), "OTP is 4821");
        alertCustomer(new EmailNotification("ravi@shop.com"), "Password changed");
    }

    // This method doesn't know or care which kind of Notification it gets.
    // Add a WhatsAppNotification class tomorrow - this method works without any change.
    static void alertCustomer(Notification notification, String message) {
        System.out.println("[alertCustomer] using " + notification.getClass().getSimpleName());
        notification.send(message);
    }
}

class Notification {
    protected final String recipient;

    Notification(String recipient) {
        this.recipient = recipient;
    }

    void send(String message) {
        System.out.println("Generic notification to " + recipient + ": " + message);
    }
}

class EmailNotification extends Notification {
    EmailNotification(String email) {
        super(email);
    }

    @Override
    void send(String message) {
        System.out.println("EMAIL to " + recipient + " | Subject: Update | Body: " + message);
    }
}

class SmsNotification extends Notification {
    SmsNotification(String phone) {
        super(phone);
    }

    @Override
    void send(String message) {
        // SMS has a length limit, so this channel shortens long messages.
        String text = message.length() > 20 ? message.substring(0, 20) + "..." : message;
        System.out.println("SMS to " + recipient + ": " + text);
    }
}

class PushNotification extends Notification {
    PushNotification(String deviceId) {
        super(deviceId);
    }

    @Override
    void send(String message) {
        System.out.println("PUSH to " + recipient + " [bell] " + message);
    }
}

/*
Output:
EMAIL to priya@shop.com | Subject: Update | Body: Your order has shipped
SMS to 98480-12345: Your order has shipp...
PUSH to device-42 [bell] Your order has shipped

EMAIL to priya@shop.com | Subject: Update | Body: Sale starts tonight!
SMS to 98480-12345: Sale starts tonight!
PUSH to device-42 [bell] Sale starts tonight!
Generic notification to generic: Sale starts tonight!

[alertCustomer] using SmsNotification
SMS to 90000-11111: OTP is 4821
[alertCustomer] using EmailNotification
EMAIL to ravi@shop.com | Subject: Update | Body: Password changed
*/

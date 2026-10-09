package com.course.patterns.patterns.adapter;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * ADAPTER (structural pattern)
 * "Make an existing class with the WRONG interface fit the interface your code expects."
 * Like a travel plug adapter: the socket (your interface) and the plug (old library) don't
 * match, the adapter sits in between and translates.
 *
 * Here: our code wants PaymentGateway.pay(account, BigDecimal amount) -> PaymentResult.
 *       The bank's OLD SDK has doTransfer(String, long amountInCents) -> int status code.
 *       LegacyBankAdapter translates units, names and error codes.
 *
 * In Spring: HandlerAdapter (lets DispatcherServlet call very different controller types),
 *            JpaVendorAdapter, MessageListenerAdapter, and every "XxxAdapter" class.
 *
 * Expected output:
 *   Paid 499.99 to ACC-1001 -> success=true, ref=LEGACY-49999
 *   Paid 0.00 to ACC-1001 -> success=false, ref=ERROR-2
 */
@Component
@Order(13)
public class AdapterDemo implements PatternDemo {

    /** The interface OUR application is written against. */
    interface PaymentGateway {
        PaymentResult pay(String account, BigDecimal amount);
    }

    record PaymentResult(boolean success, String reference) {
    }

    /** A third-party class we CANNOT change (imagine it comes from an old jar). */
    static class LegacyBankSdk {
        /** @return 0 = OK, 2 = invalid amount, 9 = unknown error */
        int doTransfer(String accountNumber, long amountInCents) {
            return amountInCents <= 0 ? 2 : 0;
        }
    }

    /** The ADAPTER: implements our interface, uses the old SDK inside. */
    static class LegacyBankAdapter implements PaymentGateway {
        private final LegacyBankSdk sdk;

        LegacyBankAdapter(LegacyBankSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public PaymentResult pay(String account, BigDecimal amount) {
            long cents = amount.movePointRight(2).longValueExact();        // translate units
            int code = sdk.doTransfer(account, cents);                       // call the old API
            return code == 0                                                 // translate the result
                    ? new PaymentResult(true, "LEGACY-" + cents)
                    : new PaymentResult(false, "ERROR-" + code);
        }
    }

    @Override
    public String title() {
        return "Adapter - make an old API fit a new interface";
    }

    @Override
    public void run() {
        PaymentGateway gateway = new LegacyBankAdapter(new LegacyBankSdk()); // code sees only PaymentGateway
        for (String amount : new String[] {"499.99", "0.00"}) {
            PaymentResult result = gateway.pay("ACC-1001", new BigDecimal(amount));
            System.out.println("Paid " + amount + " to ACC-1001 -> success=" + result.success()
                    + ", ref=" + result.reference());
        }
    }
}

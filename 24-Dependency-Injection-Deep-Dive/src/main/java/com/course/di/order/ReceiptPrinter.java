/*
 * A small helper bean that "prints" receipts. Used by ReceiptService (setter injection)
 * and LegacyAuditService (field injection).
 */
package com.course.di.order;

import org.springframework.stereotype.Component;

@Component
public class ReceiptPrinter {

    public String print(String text) {
        return "[RECEIPT] " + text;
    }
}

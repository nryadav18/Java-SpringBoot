/*
 * SETTER INJECTION.
 *
 * Spring first creates the object with the no-arg constructor, THEN calls the
 * @Autowired setter to hand over the dependency.
 *
 *  + useful for OPTIONAL dependencies or ones you may want to change later
 *  - the field cannot be final
 *  - between "new" and the setter call, the object is incomplete (printer == null)
 *
 * Here @Autowired is REQUIRED - without it Spring would never call the setter.
 */
package com.course.di.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    private ReceiptPrinter printer;   // not final - set later by the setter

    @Autowired
    public void setPrinter(ReceiptPrinter printer) {
        System.out.println("   [setter injection] setPrinter() called by Spring");
        this.printer = printer;
    }

    public String receiptFor(String orderText) {
        return printer.print(orderText);
    }
}

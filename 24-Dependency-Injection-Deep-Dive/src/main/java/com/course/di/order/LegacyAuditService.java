/*
 * FIELD INJECTION - shown so you can RECOGNISE it in older code. Avoid it in new code.
 *
 * Spring uses reflection to write straight into the private field.
 * Why it is discouraged:
 *   - the field cannot be final
 *   - dependencies are hidden (not visible in a constructor)
 *   - you cannot create a working object in a plain unit test with "new"
 *     (the field stays null unless you use reflection or a Spring test)
 *   - it makes it too easy to add 15 dependencies without noticing the class is too big
 */
package com.course.di.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LegacyAuditService {

    @Autowired                         // Spring sets this field directly via reflection
    private ReceiptPrinter receiptPrinter;

    public String audit(String event) {
        return receiptPrinter.print("AUDIT " + event);
    }
}

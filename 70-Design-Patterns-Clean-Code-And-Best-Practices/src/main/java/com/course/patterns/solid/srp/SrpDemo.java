package com.course.patterns.solid.srp;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * S - SINGLE RESPONSIBILITY PRINCIPLE
 * "A class should have only ONE reason to change."
 *
 * BAD : InvoiceManager calculates, formats AND saves. A change to the tax rule, the print
 *       layout or the storage (file -> database) all force changes to the SAME class.
 * GOOD: three small classes, each with one job. Each can change and be tested alone.
 *
 * Expected output:
 *   [bad ] INVOICE for Asha - total 1180.00 (saved to file)
 *   [good] INVOICE for Asha - total 1180.00
 *   [good] stored invoices: 1
 */
@Component
@Order(1)
public class SrpDemo implements PatternDemo {

    // ---------------------------------------------------------- BAD
    static class InvoiceManager {
        String createAndSave(String customer, List<BigDecimal> prices) {
            BigDecimal total = BigDecimal.ZERO;                       // job 1: calculate
            for (BigDecimal p : prices) {
                total = total.add(p);
            }
            total = total.multiply(new BigDecimal("1.18"));           // tax rule hidden in here
            String text = "INVOICE for " + customer + " - total " + total.setScale(2); // job 2: format
            // job 3: save (pretend we write to a file)
            return text + " (saved to file)";
        }
    }

    // ---------------------------------------------------------- GOOD
    /** Only knows the money rules. */
    static class InvoiceCalculator {
        private static final BigDecimal TAX_MULTIPLIER = new BigDecimal("1.18"); // 18% tax

        BigDecimal totalWithTax(List<BigDecimal> prices) {
            BigDecimal net = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            return net.multiply(TAX_MULTIPLIER).setScale(2);
        }
    }

    /** Only knows how an invoice LOOKS. */
    static class InvoiceFormatter {
        String format(String customer, BigDecimal total) {
            return "INVOICE for " + customer + " - total " + total;
        }
    }

    /** Only knows WHERE invoices are stored. Swap for a DB version without touching the others. */
    static class InvoiceStore {
        private final List<String> saved = new ArrayList<>();

        void save(String invoiceText) {
            saved.add(invoiceText);
        }

        int count() {
            return saved.size();
        }
    }

    @Override
    public String title() {
        return "S - Single Responsibility Principle";
    }

    @Override
    public void run() {
        List<BigDecimal> prices = List.of(new BigDecimal("400"), new BigDecimal("600"));

        System.out.println("[bad ] " + new InvoiceManager().createAndSave("Asha", prices));

        InvoiceCalculator calculator = new InvoiceCalculator();
        InvoiceFormatter formatter = new InvoiceFormatter();
        InvoiceStore store = new InvoiceStore();

        String invoice = formatter.format("Asha", calculator.totalWithTax(prices));
        store.save(invoice);
        System.out.println("[good] " + invoice);
        System.out.println("[good] stored invoices: " + store.count());
    }
}

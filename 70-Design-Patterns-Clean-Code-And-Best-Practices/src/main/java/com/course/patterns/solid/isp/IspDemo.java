package com.course.patterns.solid.isp;

import com.course.patterns.demo.PatternDemo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * I - INTERFACE SEGREGATION PRINCIPLE
 * "Clients should not be forced to depend on methods they do not use."
 * Prefer several small, focused interfaces over one fat interface.
 *
 * BAD : MultiFunctionMachine has print + scan + fax. A cheap printer must implement
 *       scan() and fax() anyway - with fake code that throws.
 * GOOD: Printer, DocumentScanner, FaxMachine. Each device implements only what it can do.
 *       (Spring does the same: CrudRepository vs PagingAndSortingRepository vs ListCrudRepository...)
 *
 * Expected output:
 *   [bad ] Printing 'report.pdf'
 *   [bad ] CRASH: This printer cannot fax
 *   [good] Printing 'report.pdf'
 *   [good] Office machine: Printing 'contract.pdf', Scanning 'id-card', Faxing to 022-555
 */
@Component
@Order(4)
public class IspDemo implements PatternDemo {

    // ---------------------------------------------------------- BAD
    interface MultiFunctionMachine {
        String print(String doc);

        String scan(String doc);

        String fax(String number);
    }

    static class CheapPrinterBad implements MultiFunctionMachine {
        public String print(String doc) { return "Printing '" + doc + "'"; }
        public String scan(String doc) { throw new UnsupportedOperationException("This printer cannot scan"); }
        public String fax(String number) { throw new UnsupportedOperationException("This printer cannot fax"); }
    }

    // ---------------------------------------------------------- GOOD
    interface Printer {
        String print(String doc);
    }

    interface DocumentScanner {
        String scan(String doc);
    }

    interface FaxMachine {
        String fax(String number);
    }

    static class CheapPrinter implements Printer {
        public String print(String doc) { return "Printing '" + doc + "'"; }
    }

    static class OfficeMachine implements Printer, DocumentScanner, FaxMachine {
        public String print(String doc) { return "Printing '" + doc + "'"; }
        public String scan(String doc) { return "Scanning '" + doc + "'"; }
        public String fax(String number) { return "Faxing to " + number; }
    }

    @Override
    public String title() {
        return "I - Interface Segregation Principle";
    }

    @Override
    public void run() {
        MultiFunctionMachine bad = new CheapPrinterBad();
        System.out.println("[bad ] " + bad.print("report.pdf"));
        try {
            bad.fax("022-555");
        } catch (UnsupportedOperationException e) {
            System.out.println("[bad ] CRASH: " + e.getMessage());
        }

        Printer printer = new CheapPrinter();
        System.out.println("[good] " + printer.print("report.pdf"));
        OfficeMachine office = new OfficeMachine();
        System.out.println("[good] Office machine: " + office.print("contract.pdf") + ", "
                + office.scan("id-card") + ", " + office.fax("022-555"));
    }
}

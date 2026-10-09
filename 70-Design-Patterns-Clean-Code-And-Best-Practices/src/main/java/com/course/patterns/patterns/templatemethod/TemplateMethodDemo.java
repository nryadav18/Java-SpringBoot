package com.course.patterns.patterns.templatemethod;

import com.course.patterns.demo.PatternDemo;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * TEMPLATE METHOD (behavioural pattern)
 * "The parent class fixes the STEPS of an algorithm; subclasses fill in some steps."
 *
 * ReportGenerator.generate() is FINAL: always header -> rows -> footer, in that order.
 * Subclasses only decide HOW each step looks (CSV vs plain text).
 *
 * In Spring, JdbcTemplate is the famous example (topic 37):
 *   the template does the boring fixed steps - get connection, prepare statement, execute,
 *   loop over the ResultSet, handle SQLException, close everything -
 *   and YOU only supply the varying part: the SQL and a RowMapper (a callback).
 *   Same idea: RestTemplate, JmsTemplate, KafkaTemplate, TransactionTemplate...
 *
 * Expected output:
 *   --- CSV ---
 *   product,qty
 *   Biryani,12
 *   Dosa,30
 *   # 2 rows
 *   --- TEXT ---
 *   SALES REPORT
 *    * Biryani ... 12
 *    * Dosa ... 30
 *   (end of report, 2 rows)
 */
@Component
@Order(16)
public class TemplateMethodDemo implements PatternDemo {

    record Sale(String product, int quantity) {
    }

    /** The template. */
    abstract static class ReportGenerator {

        /** The TEMPLATE METHOD: fixed algorithm, cannot be overridden. */
        final String generate(List<Sale> sales) {
            StringBuilder out = new StringBuilder();
            out.append(header()).append('\n');
            for (Sale sale : sales) {
                out.append(row(sale)).append('\n');
            }
            out.append(footer(sales.size()));
            return out.toString();
        }

        // The "holes" that subclasses fill in:
        protected abstract String header();

        protected abstract String row(Sale sale);

        /** A HOOK: has a default, subclasses MAY override it. */
        protected String footer(int rowCount) {
            return "# " + rowCount + " rows";
        }
    }

    static class CsvReport extends ReportGenerator {
        protected String header() { return "product,qty"; }
        protected String row(Sale sale) { return sale.product() + "," + sale.quantity(); }
    }

    static class TextReport extends ReportGenerator {
        protected String header() { return "SALES REPORT"; }
        protected String row(Sale sale) { return " * " + sale.product() + " ... " + sale.quantity(); }

        @Override
        protected String footer(int rowCount) { return "(end of report, " + rowCount + " rows)"; }
    }

    @Override
    public String title() {
        return "Template Method - fixed steps, variable details (like JdbcTemplate)";
    }

    @Override
    public void run() {
        List<Sale> sales = List.of(new Sale("Biryani", 12), new Sale("Dosa", 30));
        System.out.println("--- CSV ---");
        System.out.println(new CsvReport().generate(sales));
        System.out.println("--- TEXT ---");
        System.out.println(new TextReport().generate(sales));
    }
}

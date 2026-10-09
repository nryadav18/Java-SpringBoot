/*
 * @Lazy - create this singleton only when somebody first asks for it.
 *
 * Normally an ApplicationContext creates all singletons at startup (eager).
 * This "heavy" report service is rarely used, so we delay its creation.
 *
 * Trade-off: faster startup, but configuration errors in this bean only show up
 * later, at first use. Use @Lazy sparingly.
 */
package com.course.scopes.report;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class SalesReportService {

    public SalesReportService() {
        System.out.println("   [lazy] SalesReportService constructor runs NOW (first use)");
    }

    @PostConstruct
    void loadTemplates() {
        System.out.println("   [lazy] SalesReportService loaded its (pretend) heavy report templates");
    }

    public String dailyReport() {
        return "Daily sales report: 42 orders";
    }
}

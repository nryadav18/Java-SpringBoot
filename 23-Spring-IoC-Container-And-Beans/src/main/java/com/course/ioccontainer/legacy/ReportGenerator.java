/*
 * A plain class (NO annotation) used only for the BeanFactory vs ApplicationContext demo.
 *
 * Because it has no @Component, component scanning ignores it. In App.java we register
 * it MANUALLY with each container type to see WHEN the object gets created:
 *   - BeanFactory         -> lazily, on the first getBean() call
 *   - ApplicationContext  -> eagerly, while the container starts
 */
package com.course.ioccontainer.legacy;

public class ReportGenerator {

    public ReportGenerator() {
        System.out.println("      -> ReportGenerator constructor runs NOW");
    }

    public String monthlyReport() {
        return "Monthly report: 3 books, 1 on loan";
    }
}

/*
 * Topic 23 - The Spring IoC container and beans
 *
 * Demos (in order):
 *   1. BeanFactory vs ApplicationContext  - lazy vs eager bean creation
 *   2. Starting an AnnotationConfigApplicationContext with @ComponentScan + @Bean
 *   3. Listing beans and their names (default names, custom names, aliases)
 *   4. The different ways to call getBean(...)
 *   5. Using the fully wired beans (controller -> service -> repository)
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java
 */
package com.course.ioccontainer;

import com.course.ioccontainer.config.AppConfig;
import com.course.ioccontainer.controller.BookController;
import com.course.ioccontainer.legacy.ReportGenerator;
import com.course.ioccontainer.service.BookService;
import com.course.ioccontainer.service.FineCalculator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.NumberFormat;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {
        beanFactoryVsApplicationContext();
        componentScanningDemo();
    }

    // ---------------------------------------------------------------------------------
    // DEMO 1: BeanFactory (basic, lazy) vs ApplicationContext (full-featured, eager)
    // ---------------------------------------------------------------------------------
    private static void beanFactoryVsApplicationContext() {
        System.out.println("=== DEMO 1: BeanFactory vs ApplicationContext ===");

        // A raw BeanFactory: the most basic container. We register a bean "recipe" by hand.
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerBeanDefinition("reportGenerator", new RootBeanDefinition(ReportGenerator.class));
        System.out.println("   BeanFactory: recipe registered. Object created yet? No (lazy).");
        System.out.println("   BeanFactory: calling getBean()...");
        ReportGenerator r1 = factory.getBean(ReportGenerator.class);   // object is created HERE
        System.out.println("   " + r1.monthlyReport());

        // An ApplicationContext: creates all singleton beans while starting up (eager).
        System.out.println("   ApplicationContext: starting...");
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(ReportGenerator.class)) {   // object is created HERE
            System.out.println("   ApplicationContext: started. Now calling getBean()...");
            ReportGenerator r2 = ctx.getBean(ReportGenerator.class);   // just returns the existing one
            System.out.println("   " + r2.monthlyReport());
        }
        System.out.println();
    }

    // ---------------------------------------------------------------------------------
    // DEMOS 2-5: a real application context with component scanning
    // ---------------------------------------------------------------------------------
    private static void componentScanningDemo() {
        System.out.println("=== DEMO 2: Starting the container (scan + @Bean methods) ===");
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            System.out.println("   Container is READY.");
            System.out.println();

            System.out.println("=== DEMO 3: Which beans exist, and what are their names? ===");
            for (String name : context.getBeanDefinitionNames()) {
                if (name.startsWith("org.springframework")) {
                    continue;   // skip Spring's own internal infrastructure beans
                }
                Class<?> type = context.getType(name);
                String[] aliases = context.getAliases(name);
                System.out.println("   " + name + " -> " + (type == null ? "?" : type.getSimpleName())
                        + (aliases.length > 0 ? "   (aliases: " + Arrays.toString(aliases) + ")" : ""));
            }
            System.out.println("   Total beans incl. Spring internals: " + context.getBeanDefinitionCount());
            System.out.println();

            System.out.println("=== DEMO 4: Ways to call getBean ===");
            // (a) by TYPE - the most common and type-safe way
            BookService byType = context.getBean(BookService.class);
            // (b) by NAME - returns Object, so you must cast (not type-safe)
            BookService byName = (BookService) context.getBean("bookService");
            // (c) by NAME + TYPE - no cast needed, and checks the type
            FineCalculator calc = context.getBean("lateFeeCalculator", FineCalculator.class);
            // (d) by an ALIAS name
            NumberFormat viaAlias = context.getBean("currencyFormat", NumberFormat.class);

            System.out.println("   byType == byName ? " + (byType == byName) + "  (same singleton)");
            System.out.println("   lateFeeCalculator found: " + (calc != null));
            System.out.println("   'currencyFormat' alias is the 'moneyFormat' bean? "
                    + (viaAlias == context.getBean("moneyFormat")));
            System.out.println("   containsBean(\"fineCalculator\")? " + context.containsBean("fineCalculator")
                    + "  (it was renamed to lateFeeCalculator)");
            try {
                context.getBean("paymentService");
            } catch (NoSuchBeanDefinitionException e) {
                System.out.println("   getBean(\"paymentService\") -> " + e.getClass().getSimpleName()
                        + ": " + e.getMessage());
            }
            System.out.println();

            System.out.println("=== DEMO 5: Use the wired beans ===");
            BookController controller = context.getBean(BookController.class);
            controller.showAvailableBooks();
            controller.borrow("978-1617294945");
            controller.borrow("978-0596009205");
            controller.showLateFee(6);
            System.out.println();
            System.out.println("   Closing the container...");
        }
        System.out.println("=== Done ===");
    }
}

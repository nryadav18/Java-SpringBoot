/*
 * Topic 26 - Spring configuration, profiles and the Environment  ("QuickCart" shop)
 *
 * Demos (in order):
 *   1. @Configuration full vs lite mode (proxyBeanMethods)
 *   2. Starting the main context with a profile (@Import, @Profile, @PropertySource, @Conditional)
 *   3. The Environment: profiles, properties, property sources
 *   4. Profile-specific beans in action (DatabaseSettings, StorageService)
 *   5. @Conditional results - which optional beans exist?
 *   6. SpEL in @Value, and SpEL used directly with an ExpressionParser
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java                                   -> profile "dev" (the default we set)
 *     mvn -q compile exec:java "-Dspring.profiles.active=prod"   -> profile "prod"
 *   (In PowerShell keep the quotes around -D...; otherwise PowerShell splits the argument at the dots.)
 */
package com.course.configprofiles;

import com.course.configprofiles.config.AppConfig;
import com.course.configprofiles.config.DatabaseSettings;
import com.course.configprofiles.proxydemo.AuditLog;
import com.course.configprofiles.proxydemo.AuditedService;
import com.course.configprofiles.proxydemo.FullConfig;
import com.course.configprofiles.proxydemo.LiteConfig;
import com.course.configprofiles.service.EnvironmentReporter;
import com.course.configprofiles.service.PricingRules;
import com.course.configprofiles.storage.StorageService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

public class App {

    public static void main(String[] args) {
        proxyBeanMethodsDemo();
        mainApplicationDemo();
    }

    // ---------------------------------------------------------------------------------
    // DEMO 1: full (@Configuration) vs lite (@Configuration(proxyBeanMethods = false))
    // ---------------------------------------------------------------------------------
    private static void proxyBeanMethodsDemo() {
        System.out.println("=== DEMO 1: @Configuration full vs lite mode ===");
        try (AnnotationConfigApplicationContext full = new AnnotationConfigApplicationContext(FullConfig.class)) {
            System.out.println("   FULL config class at runtime: " + full.getBean(FullConfig.class).getClass().getSimpleName());
            System.out.println("   FULL: " + full.getBean("orderService", AuditedService.class).describe()
                    + ", " + full.getBean("paymentService", AuditedService.class).describe());
        }
        try (AnnotationConfigApplicationContext lite = new AnnotationConfigApplicationContext(LiteConfig.class)) {
            AuditLog containerLog = lite.getBean(AuditLog.class);
            System.out.println("   LITE config class at runtime: " + lite.getBean(LiteConfig.class).getClass().getSimpleName());
            System.out.println("   LITE: container's AuditLog is #" + containerLog.number() + ", but "
                    + lite.getBean("orderService", AuditedService.class).describe() + ", "
                    + lite.getBean("paymentService", AuditedService.class).describe());
            System.out.println("   LITE: " + lite.getBean("safePaymentService", AuditedService.class).describe()
                    + "  <- parameter injection is always safe");
        }
        System.out.println();
    }

    // ---------------------------------------------------------------------------------
    // DEMOS 2-6: the real application context with profiles
    // ---------------------------------------------------------------------------------
    private static void mainApplicationDemo() {
        System.out.println("=== DEMO 2: Starting the QuickCart context ===");
        // Create an EMPTY context first, configure its Environment, THEN register + refresh.
        // (The one-line constructor new AnnotationConfigApplicationContext(AppConfig.class)
        //  would already start everything - too late to change profiles.)
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Used when NO profile is activated (e.g. no -Dspring.profiles.active=...):
        context.getEnvironment().setDefaultProfiles("dev");
        // Another way to activate a profile in code:  context.getEnvironment().setActiveProfiles("prod");
        context.register(AppConfig.class);
        context.refresh();   // NOW Spring reads the config, evaluates @Profile/@Conditional and creates beans
        System.out.println("   --- context READY ---");
        System.out.println();

        try (context) {   // Java 9+: try-with-resources on an existing variable
            System.out.println("=== DEMO 3: The Environment ===");
            context.getBean(EnvironmentReporter.class).report();
            System.out.println();

            System.out.println("=== DEMO 4: Profile-specific beans ===");
            DatabaseSettings db = context.getBean(DatabaseSettings.class);
            System.out.println("   " + db);
            StorageService storage = context.getBean(StorageService.class);
            System.out.println("   storage = " + storage.name() + " -> " + storage.save("keyboard.png"));
            System.out.println("   sampleDataLoader bean present? " + context.containsBean("sampleDataLoader"));
            System.out.println();

            System.out.println("=== DEMO 5: @Conditional results ===");
            for (String name : List.of("recommendationService", "loyaltyService", "jsonExporter", "csvExporter")) {
                System.out.println("   " + name + " registered? " + context.containsBean(name));
            }
            System.out.println("   " + context.getBean("recommendationService", String.class));
            System.out.println();

            System.out.println("=== DEMO 6: SpEL ===");
            context.getBean(PricingRules.class).print();
            // SpEL also works on its own, outside @Value:
            ExpressionParser parser = new SpelExpressionParser();
            // .![ ... ] = "projection": apply the expression to every element (like stream().map())
            List<?> doubled = parser.parseExpression("{1499, 599, 8999}.![#this * 2]").getValue(List.class);
            String text = parser.parseExpression("'quick'.concat('cart').toUpperCase()").getValue(String.class);
            System.out.println("   parser: {1499, 599, 8999}.![#this * 2] -> " + doubled);
            System.out.println("   parser: 'quick'.concat('cart').toUpperCase() -> " + text);
        }
        System.out.println("=== Done ===");
    }
}

package com.course.mavendemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * App.java
 * ------------------------------------------------------------
 * The main class of the Maven demo project.
 *
 * How to run (from the folder that contains pom.xml):
 *   mvn -q compile exec:java                 (run through Maven)
 *   mvn package                              (build target/maven-demo-1.0.0.jar, runs tests first)
 *   java -jar target/maven-demo-1.0.0.jar    (run the JAR - no Maven needed)
 *
 * It also reads src/main/resources/app.properties. Maven copies that file into
 * target/classes and (because of <filtering>true</filtering>) replaces ${project.version}
 * with the real version from pom.xml.
 */
public class App {

    public static void main(String[] args) throws IOException {
        Properties props = loadAppProperties();
        System.out.println("=== " + props.getProperty("app.name") + " v" + props.getProperty("app.version") + " ===");

        LabelFormatter formatter = new LabelFormatter();

        String product = "  Ergonomic   Wireless Mouse (Black) ";
        System.out.println("Slug        : " + formatter.toSlug(product));
        System.out.println("Short title : " + formatter.shortTitle("ergonomic wireless mouse", 15));
        System.out.println("Code        : " + formatter.productCode("mouse pad", 42));

        System.out.println("Receipt:");
        System.out.println("  " + formatter.receiptLine("Wireless Mouse", "799.00", 28));
        System.out.println("  " + formatter.receiptLine("USB-C Charging Cable 2m", "349.00", 28));
        System.out.println("  " + formatter.receiptLine("Laptop Stand", "1299.00", 28));
    }

    /** Loads app.properties from the CLASSPATH (it ends up inside target/classes and inside the JAR). */
    private static Properties loadAppProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream in = App.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                props.load(in);
            }
        }
        return props;
    }
}

/*
 * Output:
 * === Shop Label Printer v1.0.0 ===
 * Slug        : ergonomic-wireless-mouse-black
 * Short title : Ergonomic wi...
 * Code        : MOU-00042
 * Receipt:
 *   Wireless Mouse        799.00
 *   USB-C Charging C...   349.00
 *   Laptop Stand         1299.00
 */

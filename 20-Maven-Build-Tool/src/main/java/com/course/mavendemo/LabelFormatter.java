package com.course.mavendemo;

import org.apache.commons.lang3.StringUtils;

/*
 * LabelFormatter.java
 * ------------------------------------------------------------
 * Business logic of our tiny "shop label printer".
 * It uses a THIRD-PARTY library: Apache Commons Lang (StringUtils).
 * We did NOT download any JAR by hand - Maven read pom.xml, downloaded
 * commons-lang3-3.17.0.jar from Maven Central into ~/.m2/repository,
 * and put it on the classpath for us. That is the main job of a build tool.
 */
public class LabelFormatter {

    /**
     * Turns a product name into a URL-friendly "slug".
     * "  Wireless   Mouse (Black) " -> "wireless-mouse-black"
     */
    public String toSlug(String productName) {
        if (StringUtils.isBlank(productName)) {          // null, "", "   " all count as blank
            return "";
        }
        String lower = StringUtils.lowerCase(productName.strip());
        String lettersAndDigits = lower.replaceAll("[^a-z0-9]+", "-");   // anything else -> '-'
        return StringUtils.strip(lettersAndDigits, "-");  // remove leading/trailing '-'
    }

    /**
     * Short title for small price tags: max 'maxLength' chars, with "..." if cut.
     * "Ergonomic Wireless Mouse" (15) -> "Ergonomic Wi..."
     */
    public String shortTitle(String productName, int maxLength) {
        return StringUtils.abbreviate(StringUtils.capitalize(productName), maxLength);
    }

    /**
     * A fixed-width receipt line: the name is padded on the right, the price on the left,
     * so all prices line up in one column.
     * ("Mouse", "799.00", 20) -> "Mouse         799.00"
     */
    public String receiptLine(String item, String price, int width) {
        int priceWidth = 9;                       // room for prices up to 99999.99
        int nameWidth = width - priceWidth;
        return StringUtils.rightPad(StringUtils.abbreviate(item, nameWidth), nameWidth)
                + StringUtils.leftPad(price, priceWidth);
    }

    /** A product code: first 3 letters upper-case + id padded with zeros. ("mouse", 42) -> "MOU-00042" */
    public String productCode(String productName, int id) {
        String prefix = StringUtils.upperCase(StringUtils.left(StringUtils.deleteWhitespace(productName), 3));
        return prefix + "-" + StringUtils.leftPad(String.valueOf(id), 5, '0');
    }
}

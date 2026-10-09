/*
 * A second custom condition: "register this bean only if a certain library is on the classpath".
 *
 * Here we check for Jackson (the JSON library). It is NOT in this project's pom.xml,
 * so the condition returns false and the JSON exporter bean is skipped.
 * Add Jackson to the pom and the bean appears - without changing any Java code.
 *
 * Spring Boot's @ConditionalOnClass works the same way: e.g. "if Tomcat is on the
 * classpath, auto-configure an embedded web server".
 */
package com.course.configprofiles.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

public class OnClassPresentCondition implements Condition {

    static final String JACKSON = "com.fasterxml.jackson.databind.ObjectMapper";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean present = ClassUtils.isPresent(JACKSON, context.getClassLoader());
        System.out.println("   [condition] Jackson on classpath? " + present + " -> "
                + (present ? "REGISTER bean" : "SKIP bean"));
        return present;
    }
}

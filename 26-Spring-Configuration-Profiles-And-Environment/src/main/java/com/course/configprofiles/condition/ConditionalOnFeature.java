/*
 * Our own conditional annotation:  @ConditionalOnFeature("recommendations")
 *
 * It is "meta-annotated" with @Conditional(OnFeatureCondition.class), so whenever Spring
 * sees @ConditionalOnFeature it runs OnFeatureCondition to decide: register the bean or not?
 *
 * This is EXACTLY how Spring Boot builds @ConditionalOnProperty, @ConditionalOnClass,
 * @ConditionalOnMissingBean ... - the secret behind auto-configuration (Topic 28).
 */
package com.course.configprofiles.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnFeatureCondition.class)
public @interface ConditionalOnFeature {

    /** Feature name; the condition checks the property features.NAME.enabled */
    String value();
}

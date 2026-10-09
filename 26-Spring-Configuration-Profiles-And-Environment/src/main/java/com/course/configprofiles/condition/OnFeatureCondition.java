/*
 * A Condition is a tiny class with ONE question: "should this bean be registered?"
 *
 * matches(...) receives:
 *   - ConditionContext       -> access to the Environment, the bean registry, the class loader
 *   - AnnotatedTypeMetadata  -> the annotations on the @Bean method / class being checked
 *
 * We read the feature name from @ConditionalOnFeature("...") and look up
 * features.<name>.enabled in the Environment (default: false).
 */
package com.course.configprofiles.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

public class OnFeatureCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnFeature.class.getName());
        String feature = (String) attributes.get("value");
        String key = "features." + feature + ".enabled";
        boolean enabled = context.getEnvironment().getProperty(key, Boolean.class, false);
        System.out.println("   [condition] " + key + " = " + enabled + " -> "
                + (enabled ? "REGISTER bean" : "SKIP bean"));
        return enabled;
    }
}

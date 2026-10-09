/*
 * Beans that exist only when a CONDITION is true.
 *
 *   recommendationService -> @ConditionalOnFeature("recommendations")  (property-based)
 *   loyaltyService        -> @ConditionalOnFeature("loyalty")          (false in dev, true in prod)
 *   jsonExporter          -> @Conditional(OnClassPresentCondition.class) (library-based)
 *   csvExporter           -> no condition, always registered
 *
 * The "beans" here are plain Strings to keep the focus on the CONDITIONS.
 */
package com.course.configprofiles.config;

import com.course.configprofiles.condition.ConditionalOnFeature;
import com.course.configprofiles.condition.OnClassPresentCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureConfig {

    @Bean
    @ConditionalOnFeature("recommendations")
    public String recommendationService() {
        return "RecommendationService: 'Customers who bought a Keyboard also bought a Mouse'";
    }

    @Bean
    @ConditionalOnFeature("loyalty")
    public String loyaltyService() {
        return "LoyaltyService: 1 point per 100 rupees";
    }

    @Bean
    @Conditional(OnClassPresentCondition.class)
    public String jsonExporter() {
        return "JsonExporter";
    }

    @Bean
    public String csvExporter() {
        return "CsvExporter";
    }
}

/*
 * A BeanPostProcessor (BPP) - a "hook" that Spring calls for EVERY bean it creates:
 *
 *   postProcessBeforeInitialization(bean, name)  -> just BEFORE @PostConstruct / init methods
 *   postProcessAfterInitialization(bean, name)   -> just AFTER them
 *
 * Whatever the method returns is what ends up in the container. So a BPP could even
 * return a DIFFERENT object - e.g. a proxy that wraps the bean. That is exactly how
 * Spring adds AOP, @Transactional, @Async ... (Topic 27).
 *
 * Here we only LOG, and only for DatabaseConnectionPool to keep the output short.
 */
package com.course.scopes.lifecycle;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof DatabaseConnectionPool) {
            System.out.println("   4. BeanPostProcessor      -> BEFORE init of '" + beanName + "'");
        }
        return bean;   // return the same bean unchanged
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DatabaseConnectionPool) {
            System.out.println("   8. BeanPostProcessor      -> AFTER init of '" + beanName + "' (a proxy could be returned here)");
        }
        return bean;
    }
}

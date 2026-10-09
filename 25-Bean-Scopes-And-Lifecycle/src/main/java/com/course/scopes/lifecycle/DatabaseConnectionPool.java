/*
 * THE FULL BEAN LIFECYCLE in one class.
 *
 * A connection pool is the classic example: it must OPEN connections after it is
 * configured, and CLOSE them when the application shuts down.
 *
 * This class has NO @Component - it is registered with a @Bean method in AppConfig, so we
 * can also show initMethod / destroyMethod. Each step prints a numbered line so you can
 * see the exact order Spring calls them:
 *
 *   1  constructor
 *   2  setter injection (dependencies / properties set)
 *   3  BeanNameAware.setBeanName   ("Aware" callbacks)
 *   4  BeanPostProcessor - before initialisation   (printed by LoggingBeanPostProcessor)
 *   5  @PostConstruct
 *   6  InitializingBean.afterPropertiesSet()
 *   7  custom init method  (@Bean(initMethod = "openConnections"))
 *   8  BeanPostProcessor - after initialisation    (printed by LoggingBeanPostProcessor)
 *   -- bean is READY and used --
 *   9  @PreDestroy
 *  10  DisposableBean.destroy()
 *  11  custom destroy method (@Bean(destroyMethod = "closeConnections"))
 *
 * In real code you would pick ONE init style (usually @PostConstruct) and ONE destroy
 * style (usually @PreDestroy). We use all of them only to show the order.
 */
package com.course.scopes.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseConnectionPool implements BeanNameAware, InitializingBean, DisposableBean {

    private final String url;
    private int poolSize;           // set by setter injection
    private int openConnections;

    public DatabaseConnectionPool(String url) {
        this.url = url;
        System.out.println("   1. constructor            -> url=" + url + ", poolSize=" + poolSize + " (not set yet!)");
    }

    @Autowired
    public void setPoolSize(PoolSettings settings) {
        this.poolSize = settings.size();
        System.out.println("   2. setter injection       -> poolSize=" + poolSize);
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("   3. BeanNameAware          -> my bean name is '" + name + "'");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("   5. @PostConstruct         -> all dependencies are ready, validate config");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("   6. afterPropertiesSet()   -> (InitializingBean interface)");
    }

    public void openConnections() {
        openConnections = poolSize;
        System.out.println("   7. initMethod             -> opened " + openConnections + " connections to " + url);
    }

    public String query(String sql) {
        return "result of [" + sql + "] using 1 of " + openConnections + " connections";
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("   9. @PreDestroy            -> stop accepting new queries");
    }

    @Override
    public void destroy() {
        System.out.println("  10. destroy()              -> (DisposableBean interface)");
    }

    public void closeConnections() {
        System.out.println("  11. destroyMethod          -> closed " + openConnections + " connections");
        openConnections = 0;
    }
}

package com.course.patterns.patterns.proxy;

import com.course.patterns.demo.PatternDemo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * PROXY (structural pattern)
 * "A stand-in object with the SAME interface that controls access to the real object."
 * The caller thinks it talks to the real object; the proxy can add work BEFORE/AFTER the call:
 * logging, timing, caching, security checks, transactions, lazy loading...
 *
 * This is the MOST IMPORTANT pattern for understanding Spring:
 *   @Transactional, @Cacheable, @Async, @PreAuthorize, @Retryable and every AOP aspect (topic 27)
 *   work because Spring gives you a PROXY instead of your bean. That is also why calling such a
 *   method from INSIDE the same class ("self-invocation") skips the proxy - and the annotation.
 *
 * Part 1: a hand-made JDK dynamic proxy that TIMES every call.
 * Part 2: Spring's ProxyFactory adding CACHING (a mini @Cacheable).
 *
 * Expected output (times vary):
 *   [jdk proxy] -> priceFor(Biryani)
 *   [jdk proxy] <- priceFor took 5x ms
 *   Price: 250.00
 *   [spring proxy] cache MISS for Dosa
 *   Price: 120.00
 *   [spring proxy] cache HIT  for Dosa
 *   Price: 120.00
 *   Real object was called 2 times in total (1 + 1), the cache saved one call.
 */
@Component
@Order(17)
public class ProxyDemo implements PatternDemo {

    /** The interface both the real object and the proxy implement. */
    public interface PriceCatalog {
        BigDecimal priceFor(String dish);
    }

    /** The REAL object: slow, e.g. it asks another service. */
    static class SlowPriceCatalog implements PriceCatalog {
        private static final Map<String, BigDecimal> PRICES =
                Map.of("Biryani", new BigDecimal("250.00"), "Dosa", new BigDecimal("120.00"));
        int calls;

        @Override
        public BigDecimal priceFor(String dish) {
            calls++;
            try {
                Thread.sleep(50); // pretend network latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return PRICES.getOrDefault(dish, BigDecimal.ZERO);
        }
    }

    @Override
    public String title() {
        return "Proxy - same interface, extra behaviour (how @Transactional/@Cacheable work)";
    }

    @Override
    public void run() {
        SlowPriceCatalog real = new SlowPriceCatalog();

        // ---------- Part 1: JDK dynamic proxy (java.lang.reflect.Proxy) ----------
        InvocationHandler timing = (proxy, method, args) -> {
            System.out.println("[jdk proxy] -> " + method.getName() + "(" + args[0] + ")");
            long start = System.nanoTime();
            Object result = method.invoke(real, args);                 // forward to the real object
            System.out.println("[jdk proxy] <- " + method.getName() + " took "
                    + (System.nanoTime() - start) / 1_000_000 + " ms");
            return result;
        };
        PriceCatalog timed = (PriceCatalog) Proxy.newProxyInstance(
                PriceCatalog.class.getClassLoader(), new Class<?>[] {PriceCatalog.class}, timing);
        System.out.println("Price: " + timed.priceFor("Biryani"));

        // ---------- Part 2: Spring's ProxyFactory (the engine behind Spring AOP) ----------
        Map<Object, Object> cache = new ConcurrentHashMap<>();
        MethodInterceptor caching = invocation -> {
            Object key = invocation.getArguments()[0];
            if (cache.containsKey(key)) {
                System.out.println("[spring proxy] cache HIT  for " + key);
                return cache.get(key);
            }
            System.out.println("[spring proxy] cache MISS for " + key);
            Object value = invocation.proceed();                       // call the real method
            cache.put(key, value);
            return value;
        };
        ProxyFactory factory = new ProxyFactory(real);
        factory.addAdvice(caching);
        PriceCatalog cached = (PriceCatalog) factory.getProxy();

        System.out.println("Price: " + cached.priceFor("Dosa"));
        System.out.println("Price: " + cached.priceFor("Dosa"));
        System.out.println("Real object was called " + real.calls
                + " times in total (1 + 1), the cache saved one call.");
    }
}

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Example04_MiniDIContainer.java
 * ------------------------------------------------------------
 * What it shows:
 *   A TINY "Spring-like" dependency-injection container in ~60 lines.
 *   This is the most important example of the topic: after it, Spring's
 *   @Component and @Autowired will not feel like magic any more.
 *
 *   Our own annotations:
 *     @MyComponent  -> "container, please create ONE object of this class"  (like Spring's @Component)
 *     @MyAutowired  -> "container, please put the right object into this field" (like Spring's @Autowired)
 *
 *   What the container does (same 3 steps as Spring, simplified):
 *     1) SCAN   : look at a list of classes, keep those annotated with @MyComponent
 *     2) CREATE : make one object of each (a "bean") using reflection
 *     3) INJECT : for every field annotated with @MyAutowired, find a bean of the
 *                 field's type and set it into the field using reflection
 *
 * Domain: an online shop - OrderController -> OrderService -> OrderRepository + EmailSender
 *
 * How to run:
 *   java Example04_MiniDIContainer.java
 */
public class Example04_MiniDIContainer {

    // =====================================================================
    // 1) Our own annotations
    // =====================================================================
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)            // on classes
    @interface MyComponent {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)           // on fields
    @interface MyAutowired {
    }

    // =====================================================================
    // 2) Application classes - notice: NO 'new' for dependencies anywhere!
    // =====================================================================
    @MyComponent
    static class OrderRepository {
        private final Map<Integer, String> orders = new HashMap<>();

        void save(int id, String item) {
            orders.put(id, item);
            System.out.println("    [OrderRepository] saved order " + id + " (" + item + ")");
        }
    }

    // An interface + implementation, like real Spring apps ("program to an interface").
    interface Notifier {
        void notifyCustomer(String message);
    }

    @MyComponent
    static class EmailSender implements Notifier {
        @Override
        public void notifyCustomer(String message) {
            System.out.println("    [EmailSender] email: " + message);
        }
    }

    @MyComponent
    static class OrderService {
        @MyAutowired
        private OrderRepository repository;     // will be injected by the container

        @MyAutowired
        private Notifier notifier;              // interface type! container finds EmailSender

        void placeOrder(int id, String item) {
            repository.save(id, item);
            notifier.notifyCustomer("Your order " + id + " for " + item + " is confirmed");
        }
    }

    @MyComponent
    static class OrderController {
        @MyAutowired
        private OrderService orderService;

        void handleRequest(int id, String item) {
            System.out.println("  [OrderController] POST /orders " + id + " " + item);
            orderService.placeOrder(id, item);
        }
    }

    // Not annotated -> the container must ignore it
    static class HelperNotABean {
    }

    // =====================================================================
    // 3) The container itself
    // =====================================================================
    static class MiniContainer {
        // The "bean registry": class -> the single object of that class
        private final Map<Class<?>, Object> beans = new HashMap<>();

        MiniContainer(List<Class<?>> classesToScan) throws Exception {
            // ---- Step 1 + 2: SCAN and CREATE ----
            for (Class<?> clazz : classesToScan) {
                if (clazz.isAnnotationPresent(MyComponent.class)) {
                    Object bean = clazz.getDeclaredConstructor().newInstance(); // reflection 'new'
                    beans.put(clazz, bean);
                    System.out.println("Created bean: " + clazz.getSimpleName());
                } else {
                    System.out.println("Skipped (no @MyComponent): " + clazz.getSimpleName());
                }
            }

            // ---- Step 3: INJECT ----
            for (Object bean : beans.values()) {
                for (Field field : bean.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(MyAutowired.class)) {
                        Object dependency = findBeanOfType(field.getType());
                        field.setAccessible(true);           // fields are private
                        field.set(bean, dependency);         // the actual "injection"
                        System.out.println("Injected " + dependency.getClass().getSimpleName()
                                + " into " + bean.getClass().getSimpleName() + "." + field.getName());
                    }
                }
            }
        }

        // Find a bean that IS-A 'type' (same class, subclass, or implementation of an interface)
        private Object findBeanOfType(Class<?> type) {
            for (Object bean : beans.values()) {
                if (type.isAssignableFrom(bean.getClass())) {
                    return bean;
                }
            }
            throw new IllegalStateException("No bean found for type " + type.getSimpleName());
        }

        // Like Spring's context.getBean(OrderController.class)
        <T> T getBean(Class<T> type) {
            return type.cast(findBeanOfType(type));
        }
    }

    // =====================================================================
    // 4) Start the "application"
    // =====================================================================
    public static void main(String[] args) throws Exception {
        System.out.println("=== Starting MiniContainer ===");
        // Real Spring scans packages on disk. To keep it simple we hand it a list.
        MiniContainer container = new MiniContainer(List.of(
                OrderController.class,
                OrderService.class,
                OrderRepository.class,
                EmailSender.class,
                HelperNotABean.class));

        System.out.println("\n=== Application ready, handling a request ===");
        OrderController controller = container.getBean(OrderController.class);
        controller.handleRequest(101, "Wireless Mouse");

        // Beans are singletons: asking twice gives the SAME object
        System.out.println("\nSame OrderService both times? "
                + (container.getBean(OrderService.class) == container.getBean(OrderService.class)));
    }
}

/*
 * Output (the order of the "Injected ..." lines may differ, because HashMap has no order):
 * === Starting MiniContainer ===
 * Created bean: OrderController
 * Created bean: OrderService
 * Created bean: OrderRepository
 * Created bean: EmailSender
 * Skipped (no @MyComponent): HelperNotABean
 * Injected OrderService into OrderController.orderService
 * Injected OrderRepository into OrderService.repository
 * Injected EmailSender into OrderService.notifier
 *
 * === Application ready, handling a request ===
 *   [OrderController] POST /orders 101 Wireless Mouse
 *     [OrderRepository] saved order 101 (Wireless Mouse)
 *     [EmailSender] email: Your order 101 for Wireless Mouse is confirmed
 *
 * Same OrderService both times? true
 */

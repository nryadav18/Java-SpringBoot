/*
 * Example03_TerminalOperations.java
 * ------------------------------------------------------------
 * WHAT IT SHOWS:
 *   Terminal operations - they END the pipeline and produce a result
 *   (or a side effect). After a terminal op the stream is used up.
 *
 *   forEach, collect(toList / toSet / toMap), toList() (Java 16+),
 *   count, reduce, min, max, anyMatch, allMatch, noneMatch,
 *   findFirst, findAny
 *
 *   Domain: online shop orders.
 *
 * HOW TO RUN:
 *   java Example03_TerminalOperations.java
 */
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Example03_TerminalOperations {

    static class Order {
        private final int id;
        private final String customer;
        private final double amount;
        private final String status;   // NEW, SHIPPED, DELIVERED, CANCELLED

        Order(int id, String customer, double amount, String status) {
            this.id = id;
            this.customer = customer;
            this.amount = amount;
            this.status = status;
        }

        int getId() { return id; }
        String getCustomer() { return customer; }
        double getAmount() { return amount; }
        String getStatus() { return status; }

        @Override
        public String toString() { return "#" + id + "(" + customer + ", " + amount + ")"; }
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(101, "Asha", 1200.0, "DELIVERED"),
                new Order(102, "Ravi", 450.0, "NEW"),
                new Order(103, "Asha", 3000.0, "SHIPPED"),
                new Order(104, "John", 799.0, "CANCELLED"),
                new Order(105, "Meena", 150.0, "DELIVERED"));

        // forEach(Consumer): do something with each element (side effect)
        System.out.println("forEach:");
        orders.stream().forEach(o -> System.out.println("  " + o));

        // collect(Collectors.toList()) - the classic way to get a List back
        List<Integer> ids = orders.stream().map(Order::getId).collect(Collectors.toList());
        System.out.println("toList (collector) : " + ids);

        // .toList() - Java 16+ shortcut. NOTE: the returned list is UNMODIFIABLE.
        List<String> customers = orders.stream().map(Order::getCustomer).toList();
        System.out.println("toList() Java 16+  : " + customers);

        // toSet - duplicates removed. We wrap in a TreeSet so printing is sorted.
        Set<String> uniqueCustomers = orders.stream()
                .map(Order::getCustomer)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("unique customers   : " + uniqueCustomers);

        // toMap(keyMapper, valueMapper) - id -> amount
        Map<Integer, Double> amountById = orders.stream()
                .collect(Collectors.toMap(Order::getId, Order::getAmount));
        System.out.println("toMap id->amount   : " + amountById);

        // count()
        long delivered = orders.stream().filter(o -> o.getStatus().equals("DELIVERED")).count();
        System.out.println("delivered count    : " + delivered);

        // reduce(identity, accumulator): combine all elements into ONE value
        // Start with 0.0, then add each amount: ((((0+1200)+450)+3000)+799)+150
        double total = orders.stream().map(Order::getAmount).reduce(0.0, (a, b) -> a + b);
        System.out.println("reduce total       : " + total);
        // Same with a method reference:
        double total2 = orders.stream().map(Order::getAmount).reduce(0.0, Double::sum);
        System.out.println("reduce Double::sum : " + total2);
        // reduce WITHOUT identity returns Optional (stream might be empty)
        Optional<String> allNames = orders.stream().map(Order::getCustomer).reduce((a, b) -> a + "," + b);
        System.out.println("reduce no identity : " + allNames.get());

        // min / max with a Comparator -> Optional
        Order biggest = orders.stream().max(Comparator.comparing(Order::getAmount)).get();
        Order smallest = orders.stream().min(Comparator.comparing(Order::getAmount)).get();
        System.out.println("max / min order    : " + biggest + " / " + smallest);

        // anyMatch / allMatch / noneMatch -> boolean (they short-circuit = stop early)
        System.out.println("any cancelled?     : " + orders.stream().anyMatch(o -> o.getStatus().equals("CANCELLED")));
        System.out.println("all above 100?     : " + orders.stream().allMatch(o -> o.getAmount() > 100));
        System.out.println("none above 5000?   : " + orders.stream().noneMatch(o -> o.getAmount() > 5000));

        // findFirst -> Optional (first in encounter order)
        Optional<Order> firstNew = orders.stream().filter(o -> o.getStatus().equals("NEW")).findFirst();
        System.out.println("first NEW order    : " + firstNew.orElse(null));

        // findAny -> Optional (ANY match; may differ in parallel streams)
        Optional<Order> anyRefund = orders.stream().filter(o -> o.getStatus().equals("REFUNDED")).findAny();
        System.out.println("any REFUNDED?      : " + anyRefund.isPresent());
    }
}

/* Output:
forEach:
  #101(Asha, 1200.0)
  #102(Ravi, 450.0)
  #103(Asha, 3000.0)
  #104(John, 799.0)
  #105(Meena, 150.0)
toList (collector) : [101, 102, 103, 104, 105]
toList() Java 16+  : [Asha, Ravi, Asha, John, Meena]
unique customers   : [Asha, John, Meena, Ravi]
toMap id->amount   : {101=1200.0, 102=450.0, 103=3000.0, 104=799.0, 105=150.0}
delivered count    : 2
reduce total       : 5599.0
reduce Double::sum : 5599.0
reduce no identity : Asha,Ravi,Asha,John,Meena
max / min order    : #103(Asha, 3000.0) / #105(Meena, 150.0)
any cancelled?     : true
all above 100?     : true
none above 5000?   : true
first NEW order    : #102(Ravi, 450.0)
any REFUNDED?      : false
*/

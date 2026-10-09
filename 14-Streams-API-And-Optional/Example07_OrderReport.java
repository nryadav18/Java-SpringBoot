/*
 * Example07_OrderReport.java
 * ------------------------------------------------------------
 * MINI PROGRAM: a sales report for an online shop, built ONLY with streams.
 *
 * WHAT IT SHOWS:
 *   Real-world stream usage combining everything from this topic:
 *   filter, map, flatMap, sorted, limit, groupingBy, partitioningBy,
 *   joining, reduce/sum, Optional, Comparator.comparing.
 *   Also shows converting "entities" to small summary objects - the same
 *   idea you'll use to map JPA entities to DTOs in Spring.
 *
 * HOW TO RUN:
 *   java Example07_OrderReport.java
 */
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Example07_OrderReport {

    static class OrderItem {
        private final String product;
        private final String category;
        private final int quantity;
        private final double unitPrice;

        OrderItem(String product, String category, int quantity, double unitPrice) {
            this.product = product;
            this.category = category;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        String getProduct() { return product; }
        String getCategory() { return category; }
        int getQuantity() { return quantity; }
        double lineTotal() { return quantity * unitPrice; }
    }

    static class Order {
        private final int id;
        private final String customer;
        private final String status;
        private final List<OrderItem> items;

        Order(int id, String customer, String status, List<OrderItem> items) {
            this.id = id;
            this.customer = customer;
            this.status = status;
            this.items = items;
        }

        int getId() { return id; }
        String getCustomer() { return customer; }
        String getStatus() { return status; }
        List<OrderItem> getItems() { return items; }

        // Order total = sum of its line totals (a stream inside a method!)
        double getTotal() {
            return items.stream().mapToDouble(OrderItem::lineTotal).sum();
        }
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(1, "Asha", "DELIVERED", List.of(
                        new OrderItem("Laptop", "Electronics", 1, 55000),
                        new OrderItem("Mouse", "Electronics", 2, 500))),
                new Order(2, "Ravi", "DELIVERED", List.of(
                        new OrderItem("Rice 5kg", "Grocery", 2, 350),
                        new OrderItem("Olive Oil", "Grocery", 1, 900))),
                new Order(3, "Asha", "CANCELLED", List.of(
                        new OrderItem("Headphones", "Electronics", 1, 2000))),
                new Order(4, "John", "DELIVERED", List.of(
                        new OrderItem("Java Book", "Books", 1, 650),
                        new OrderItem("Mouse", "Electronics", 1, 500))),
                new Order(5, "Meena", "SHIPPED", List.of(
                        new OrderItem("Rice 5kg", "Grocery", 4, 350))));

        // Only completed (non-cancelled) orders count as revenue
        List<Order> valid = orders.stream()
                .filter(o -> !o.getStatus().equals("CANCELLED"))
                .collect(Collectors.toList());

        System.out.println("===== SALES REPORT =====");

        // 1. Total revenue
        double revenue = valid.stream().mapToDouble(Order::getTotal).sum();
        System.out.printf("Total revenue          : Rs.%.2f%n", revenue);

        // 2. Revenue per customer (sorted by name)
        Map<String, Double> perCustomer = valid.stream()
                .collect(Collectors.groupingBy(Order::getCustomer, TreeMap::new,
                        Collectors.summingDouble(Order::getTotal)));
        System.out.println("Revenue per customer   : " + perCustomer);

        // 3. Best customer (Optional because the map could be empty)
        Optional<Map.Entry<String, Double>> best = perCustomer.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        System.out.println("Best customer          : " + best.map(Map.Entry::getKey).orElse("nobody"));

        // 4. Quantity sold per product - flatMap orders -> items
        Map<String, Integer> qtyPerProduct = valid.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getProduct, TreeMap::new,
                        Collectors.summingInt(OrderItem::getQuantity)));
        System.out.println("Quantity per product   : " + qtyPerProduct);

        // 5. Revenue per category
        Map<String, Double> perCategory = valid.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(OrderItem::getCategory, TreeMap::new,
                        Collectors.summingDouble(OrderItem::lineTotal)));
        System.out.println("Revenue per category   : " + perCategory);

        // 6. Top 2 orders by value -> "summary strings" (like mapping entity -> DTO)
        List<String> top2 = valid.stream()
                .sorted(Comparator.comparing(Order::getTotal).reversed())
                .limit(2)
                .map(o -> "#" + o.getId() + " " + o.getCustomer() + " Rs." + o.getTotal())
                .collect(Collectors.toList());
        System.out.println("Top 2 orders           : " + top2);

        // 7. Big vs small orders (partitioningBy)
        Map<Boolean, List<Integer>> bigOrders = valid.stream()
                .collect(Collectors.partitioningBy(o -> o.getTotal() >= 2000,
                        Collectors.mapping(Order::getId, Collectors.toList())));
        System.out.println("Orders >= 2000 (ids)   : " + bigOrders.get(true));
        System.out.println("Orders <  2000 (ids)   : " + bigOrders.get(false));

        // 8. Customers who cancelled something
        String cancelled = orders.stream()
                .filter(o -> o.getStatus().equals("CANCELLED"))
                .map(Order::getCustomer)
                .distinct()
                .collect(Collectors.joining(", "));
        System.out.println("Customers who cancelled: " + cancelled);

        // 9. Did anyone buy Books?
        boolean anyBooks = valid.stream()
                .flatMap(o -> o.getItems().stream())
                .anyMatch(i -> i.getCategory().equals("Books"));
        System.out.println("Any book sold?         : " + anyBooks);
    }
}

/* Output:
===== SALES REPORT =====
Total revenue          : Rs.60150.00
Revenue per customer   : {Asha=56000.0, John=1150.0, Meena=1400.0, Ravi=1600.0}
Best customer          : Asha
Quantity per product   : {Java Book=1, Laptop=1, Mouse=3, Olive Oil=1, Rice 5kg=6}
Revenue per category   : {Books=650.0, Electronics=56500.0, Grocery=3000.0}
Top 2 orders           : [#1 Asha Rs.56000.0, #2 Ravi Rs.1600.0]
Orders >= 2000 (ids)   : [1]
Orders <  2000 (ids)   : [2, 4, 5]
Customers who cancelled: Asha
Any book sold?         : true
*/

/*
 * Example06_FoodDeliveryFilters.java
 * ------------------------------------------------------------
 * MINI PROGRAM: a food-delivery app's restaurant search.
 *
 * WHAT IT SHOWS:
 *   "Passing behaviour as data". The user picks filters (veg only,
 *   rating 4+, fast delivery) and we COMBINE Predicate lambdas at
 *   runtime instead of writing one method per combination.
 *   Also uses Function (to format), Consumer (to print),
 *   Supplier (default message) and method references.
 *
 * HOW TO RUN:
 *   java Example06_FoodDeliveryFilters.java
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Example06_FoodDeliveryFilters {

    static class Restaurant {
        private final String name;
        private final boolean pureVeg;
        private final double rating;
        private final int deliveryMinutes;

        Restaurant(String name, boolean pureVeg, double rating, int deliveryMinutes) {
            this.name = name;
            this.pureVeg = pureVeg;
            this.rating = rating;
            this.deliveryMinutes = deliveryMinutes;
        }

        String getName() { return name; }
        boolean isPureVeg() { return pureVeg; }
        double getRating() { return rating; }
        int getDeliveryMinutes() { return deliveryMinutes; }
    }

    // Ready-made, reusable filter "building blocks".
    static final Predicate<Restaurant> VEG_ONLY = Restaurant::isPureVeg;
    static final Predicate<Restaurant> RATING_4_PLUS = r -> r.getRating() >= 4.0;
    static final Predicate<Restaurant> FAST_DELIVERY = r -> r.getDeliveryMinutes() <= 30;

    public static void main(String[] args) {
        List<Restaurant> all = List.of(
                new Restaurant("Saravana Bhavan", true, 4.5, 25),
                new Restaurant("Biryani House", false, 4.2, 40),
                new Restaurant("Pizza Point", false, 3.8, 20),
                new Restaurant("Green Leaf Cafe", true, 3.9, 15),
                new Restaurant("Tandoori Nights", false, 4.7, 30));

        // How each restaurant is shown on screen (Function = convert)
        Function<Restaurant, String> card =
                r -> String.format("%-16s %s  %.1f*  %d min",
                        r.getName(), r.isPureVeg() ? "VEG    " : "NON-VEG", r.getRating(), r.getDeliveryMinutes());

        // What to do with each line (Consumer = use)
        Consumer<String> show = line -> System.out.println("  " + line);

        // Message when nothing matches (Supplier = give me)
        Supplier<String> noResults = () -> "  (no restaurants match - try removing a filter)";

        // The user's choices -> one combined Predicate built at runtime
        search("Veg AND rating 4+", all, VEG_ONLY.and(RATING_4_PLUS), card, show, noResults);
        search("Fast delivery, best rated first", all, FAST_DELIVERY, card, show, noResults);
        search("Non-veg AND fast AND 4+", all, VEG_ONLY.negate().and(FAST_DELIVERY).and(RATING_4_PLUS), card, show, noResults);
        search("Veg AND slower than 30 min", all, VEG_ONLY.and(FAST_DELIVERY.negate()), card, show, noResults);
    }

    static void search(String title, List<Restaurant> all, Predicate<Restaurant> filter,
                       Function<Restaurant, String> formatter, Consumer<String> printer,
                       Supplier<String> whenEmpty) {
        System.out.println(title + ":");
        List<Restaurant> found = new ArrayList<>();
        for (Restaurant r : all) {
            if (filter.test(r)) {
                found.add(r);
            }
        }
        if (found.isEmpty()) {
            System.out.println(whenEmpty.get());
            return;
        }
        // Highest rating first, using Comparator helpers + method reference
        found.sort(Comparator.comparing(Restaurant::getRating).reversed());
        for (Restaurant r : found) {
            printer.accept(formatter.apply(r));
        }
    }
}

/* Output:
Veg AND rating 4+:
  Saravana Bhavan  VEG      4.5*  25 min
Fast delivery, best rated first:
  Tandoori Nights  NON-VEG  4.7*  30 min
  Saravana Bhavan  VEG      4.5*  25 min
  Green Leaf Cafe  VEG      3.9*  15 min
  Pizza Point      NON-VEG  3.8*  20 min
Non-veg AND fast AND 4+:
  Tandoori Nights  NON-VEG  4.7*  30 min
Veg AND slower than 30 min:
  (no restaurants match - try removing a filter)
*/

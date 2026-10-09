/*
 * Example02_GenericClasses.java
 *
 * What this file demonstrates:
 *   - Writing your own generic class:  class Box<T> { ... }
 *   - One type parameter (Box<T>), two type parameters (Pair<K, V>)
 *   - Type parameter naming conventions: T (type), E (element), K (key), V (value), ID, R (result)
 *   - The diamond operator <> and "var"
 *   - A generic class that holds a list: a Shelf<T> that only accepts one kind of item
 *   - A generic result wrapper ApiResponse<T> - the same idea you'll use in Spring REST APIs
 *   - Primitives are not allowed: Box<int> is illegal -> use Box<Integer>
 *
 * Domain: an online shop.
 *
 * How to run:
 *   java Example02_GenericClasses.java
 *
 * Output:
 *   Box<String>  holds: Gift card
 *   Box<Integer> holds: 500 (doubled: 1000)
 *   Box<Product> holds: Product{Headphones, Rs.1999.0}
 *   Is the box empty? false
 *   Pair: SKU-101 -> 25
 *   Pair: Asha -> Product{Phone, Rs.15999.0}
 *   Shelf of books: [Clean Code, Effective Java] (2 items)
 *   Shelf of products total value: Rs.2498.0
 *   OK   response: ApiResponse{success=true, data=Product{Mouse, Rs.499.0}, error=null}
 *   FAIL response: ApiResponse{success=false, data=null, error=Product 99 not found}
 */
import java.util.ArrayList;
import java.util.List;

public class Example02_GenericClasses {

    public static void main(String[] args) {
        // T = String
        Box<String> giftBox = new Box<>("Gift card");
        System.out.println("Box<String>  holds: " + giftBox.get());

        // T = Integer (NOT int - generics only work with objects)
        Box<Integer> cashBox = new Box<>(500);
        int doubled = cashBox.get() * 2;            // auto-unboxing Integer -> int
        System.out.println("Box<Integer> holds: " + cashBox.get() + " (doubled: " + doubled + ")");
        // Box<int> intBox;   // compile error: unexpected type; required: reference, found: int

        // T = our own class
        var productBox = new Box<>(new Product("Headphones", 1999));   // var: type inferred as Box<Product>
        System.out.println("Box<Product> holds: " + productBox.get());
        System.out.println("Is the box empty? " + productBox.isEmpty());

        // Two type parameters
        Pair<String, Integer> stock = new Pair<>("SKU-101", 25);
        System.out.println("Pair: " + stock.getKey() + " -> " + stock.getValue());
        Pair<String, Product> wishlist = new Pair<>("Asha", new Product("Phone", 15999));
        System.out.println("Pair: " + wishlist.getKey() + " -> " + wishlist.getValue());

        // A generic container of many items
        Shelf<String> bookShelf = new Shelf<>();
        bookShelf.put("Clean Code");
        bookShelf.put("Effective Java");
        // bookShelf.put(new Product("Pen", 10));   // compile error: this shelf is for Strings only
        System.out.println("Shelf of books: " + bookShelf.items() + " (" + bookShelf.size() + " items)");

        Shelf<Product> productShelf = new Shelf<>();
        productShelf.put(new Product("Keyboard", 1999));
        productShelf.put(new Product("Mouse", 499));
        double total = 0;
        for (Product p : productShelf.items()) {     // items() returns List<Product> - no casts
            total += p.getPrice();
        }
        System.out.println("Shelf of products total value: Rs." + total);

        // A generic wrapper for API results
        ApiResponse<Product> ok = ApiResponse.success(new Product("Mouse", 499));
        ApiResponse<Product> fail = ApiResponse.failure("Product 99 not found");
        System.out.println("OK   response: " + ok);
        System.out.println("FAIL response: " + fail);
    }
}

// ---------- 1 type parameter ----------
class Box<T> {
    private final T item;         // T is used like a normal type inside the class

    Box(T item) {
        this.item = item;
    }

    T get() {
        return item;
    }

    boolean isEmpty() {
        return item == null;
    }
}

// ---------- 2 type parameters ----------
class Pair<K, V> {
    private final K key;
    private final V value;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getKey()   { return key; }
    V getValue() { return value; }
}

// ---------- a generic container ----------
class Shelf<E> {                                  // E = Element (convention for collections)
    private final List<E> items = new ArrayList<>();

    void put(E item) {
        items.add(item);
    }

    List<E> items() {
        return List.copyOf(items);                // hand out a read-only copy (Topic 11 tip)
    }

    int size() {
        return items.size();
    }
}

// ---------- a generic result wrapper ----------
class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String error;

    private ApiResponse(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // Static GENERIC factory methods: note the <T> before the return type (explained in Example03)
    static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    static <T> ApiResponse<T> failure(String error) {
        return new ApiResponse<>(false, null, error);
    }

    @Override
    public String toString() {
        return "ApiResponse{success=" + success + ", data=" + data + ", error=" + error + "}";
    }
}

class Product {
    private final String name;
    private final double price;

    Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    double getPrice() { return price; }

    @Override
    public String toString() {
        return "Product{" + name + ", Rs." + price + "}";
    }
}

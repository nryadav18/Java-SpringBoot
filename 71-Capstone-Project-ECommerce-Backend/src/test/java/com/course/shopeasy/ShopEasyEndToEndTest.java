package com.course.shopeasy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.course.shopeasy.config.DataSeeder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * FULL integration test (topic 46): the real application starts on a random port with the H2 database,
 * Flyway migrations, the data seeder, security, caching - everything. We talk to it over real HTTP
 * exactly like a mobile app or a curl command would.
 *
 * Each test registers its own fresh customer (random e-mail) so tests never depend on each other.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShopEasyEndToEndTest {

    @Autowired
    private TestRestTemplate http;

    @Test
    @DisplayName("register -> browse -> add to cart -> place order -> stock reduced -> cancel -> stock restored")
    void fullShoppingFlow() {
        String token = registerNewCustomer();

        // Browse: search the catalog (public, paginated)
        ResponseEntity<JsonNode> search = http.getForEntity("/api/v1/products?q=bottle&size=5", JsonNode.class);
        assertThat(search.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode bottle = search.getBody().get("content").get(0);
        long productId = bottle.get("id").asLong();
        assertThat(bottle.get("name").asText()).isEqualTo("Steel Water Bottle");

        // Read the product once -> it is now in the cache
        int stockBefore = getProduct(productId).get("stockQuantity").asInt();

        // Add 2 to the cart
        ResponseEntity<JsonNode> cart = exchange(HttpMethod.POST, "/api/v1/cart/items", token,
                Map.of("productId", productId, "quantity", 2));
        assertThat(cart.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cart.getBody().get("totalQuantity").asInt()).isEqualTo(2);

        // Place the order
        ResponseEntity<JsonNode> order = exchange(HttpMethod.POST, "/api/v1/orders", token,
                Map.of("shippingAddress", "12 MG Road, Bengaluru"));
        assertThat(order.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(order.getHeaders().getLocation()).isNotNull();
        assertThat(order.getBody().get("status").asText()).isEqualTo("PENDING");
        assertThat(order.getBody().get("totalAmount").decimalValue()).isEqualByComparingTo("1198.00"); // 2 x 599
        long orderId = order.getBody().get("id").asLong();

        // Stock went down - and we see the NEW value, so the cache entry was evicted correctly
        assertThat(getProduct(productId).get("stockQuantity").asInt()).isEqualTo(stockBefore - 2);

        // Cart is empty after checkout
        assertThat(exchange(HttpMethod.GET, "/api/v1/cart", token, null).getBody().get("totalQuantity").asInt()).isZero();

        // The order appears in "my orders"
        ResponseEntity<JsonNode> myOrders = exchange(HttpMethod.GET, "/api/v1/orders", token, null);
        assertThat(myOrders.getBody().get("totalElements").asInt()).isEqualTo(1);

        // Cancel -> stock is back
        ResponseEntity<JsonNode> cancelled = exchange(HttpMethod.POST, "/api/v1/orders/" + orderId + "/cancel", token, null);
        assertThat(cancelled.getBody().get("status").asText()).isEqualTo("CANCELLED");
        assertThat(getProduct(productId).get("stockQuantity").asInt()).isEqualTo(stockBefore);
    }

    @Test
    @DisplayName("two customers want the last 3 items: the first order wins, the second gets 409 and nothing changes")
    void insufficientStockAtCheckout() {
        // Admin creates a product with only 3 units
        String admin = login(DataSeeder.ADMIN_EMAIL, DataSeeder.ADMIN_PASSWORD);
        String sku = "TEST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ResponseEntity<JsonNode> created = exchange(HttpMethod.POST, "/api/v1/products", admin, Map.of(
                "sku", sku, "name", "Limited Edition Mug", "price", 250, "stockQuantity", 3, "categoryId", 3));
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        long productId = created.getBody().get("id").asLong();

        // Both customers put all 3 in their carts (allowed: the cart does not reserve stock)
        String alice = registerNewCustomer();
        String bob = registerNewCustomer();
        exchange(HttpMethod.POST, "/api/v1/cart/items", alice, Map.of("productId", productId, "quantity", 3));
        exchange(HttpMethod.POST, "/api/v1/cart/items", bob, Map.of("productId", productId, "quantity", 3));

        // Alice checks out first
        assertThat(exchange(HttpMethod.POST, "/api/v1/orders", alice, Map.of("shippingAddress", "Alice street 1"))
                .getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Bob is too late
        ResponseEntity<JsonNode> bobsOrder = exchange(HttpMethod.POST, "/api/v1/orders", bob,
                Map.of("shippingAddress", "Bob street 2"));
        assertThat(bobsOrder.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(bobsOrder.getBody().get("title").asText()).isEqualTo("Insufficient stock");
        assertThat(bobsOrder.getBody().get("available").asInt()).isZero();

        // Rolled back: Bob's cart still has the item, stock is 0 (not negative)
        assertThat(exchange(HttpMethod.GET, "/api/v1/cart", bob, null).getBody().get("totalQuantity").asInt()).isEqualTo(3);
        assertThat(getProduct(productId).get("stockQuantity").asInt()).isZero();
    }

    @Test
    @DisplayName("security: 401 without token, 403 with the wrong role, 401 for a wrong password")
    void securityRules() {
        assertThat(http.getForEntity("/api/v1/cart", JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        String customer = login(DataSeeder.CUSTOMER_EMAIL, DataSeeder.CUSTOMER_PASSWORD);
        ResponseEntity<JsonNode> forbidden = exchange(HttpMethod.POST, "/api/v1/categories", customer,
                Map.of("name", "Hacked"));
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<JsonNode> badLogin = http.postForEntity("/api/v1/auth/login",
                Map.of("email", DataSeeder.CUSTOMER_EMAIL, "password", "wrong-password"), JsonNode.class);
        assertThat(badLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(badLogin.getBody().get("detail").asText()).isEqualTo("Invalid email or password");

        String admin = login(DataSeeder.ADMIN_EMAIL, DataSeeder.ADMIN_PASSWORD);
        assertThat(exchange(HttpMethod.GET, "/api/v1/admin/orders", admin, null).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange(HttpMethod.GET, "/api/v1/admin/orders", customer, null).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("registering twice with the same e-mail gives 409, invalid input gives 400 with field errors")
    void registrationValidation() {
        ResponseEntity<JsonNode> duplicate = http.postForEntity("/api/v1/auth/register",
                Map.of("fullName", "Copy Cat", "email", DataSeeder.CUSTOMER_EMAIL, "password", "Secret@123"), JsonNode.class);
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<JsonNode> invalid = http.postForEntity("/api/v1/auth/register",
                Map.of("fullName", "", "email", "not-an-email", "password", "short"), JsonNode.class);
        assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(invalid.getBody().get("errors").has("email")).isTrue();
        assertThat(invalid.getBody().get("errors").has("password")).isTrue();
    }

    // ------------------------------------------------------------------ helpers

    private String registerNewCustomer() {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        ResponseEntity<JsonNode> response = http.postForEntity("/api/v1/auth/register",
                Map.of("fullName", "Test User", "email", email, "password", "Secret@123"), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody().get("accessToken").asText();
    }

    private String login(String email, String password) {
        ResponseEntity<JsonNode> response = http.postForEntity("/api/v1/auth/login",
                Map.of("email", email, "password", password), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().get("accessToken").asText();
    }

    private JsonNode getProduct(long id) {
        ResponseEntity<JsonNode> response = http.getForEntity("/api/v1/products/" + id, JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    private ResponseEntity<JsonNode> exchange(HttpMethod method, String url, String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return http.exchange(url, method, new HttpEntity<>(body, headers), JsonNode.class);
    }
}

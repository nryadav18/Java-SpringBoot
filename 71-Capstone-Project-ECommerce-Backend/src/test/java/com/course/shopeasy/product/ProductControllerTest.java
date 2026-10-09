package com.course.shopeasy.product;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.course.shopeasy.common.exception.ResourceNotFoundException;
import com.course.shopeasy.common.web.PageResponse;
import com.course.shopeasy.security.JwtService;
import com.course.shopeasy.security.SecurityConfig;
import com.course.shopeasy.security.SecurityProblemHandler;
import com.course.shopeasy.security.ShopEasyUserDetailsService;

/**
 * WEB SLICE test (topic 46): @WebMvcTest starts only the MVC layer - this controller, the
 * @RestControllerAdvice, Jackson, filters - but NO services, repositories or database.
 *
 * - ProductService is replaced by a Mockito mock with @MockitoBean (Spring Boot 3.4+; it replaces @MockBean).
 * - Our real SecurityConfig is imported, so we test the REAL rules: 401 / 403 / 201.
 * - @WithMockUser puts a fake logged-in user into the security context, so we need no real JWT here.
 */
@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, SecurityProblemHandler.class})
class ProductControllerTest {

    private static final String VALID_PRODUCT_JSON = """
            {
              "sku": "SPORT-001",
              "name": "Yoga Mat",
              "description": "6 mm thick",
              "price": 1499.00,
              "stockQuantity": 25,
              "categoryId": 1
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    // SecurityConfig needs these two beans to build the JWT filter:
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private ShopEasyUserDetailsService userDetailsService;

    @Test
    void anyoneCanListProducts() throws Exception {
        when(productService.search(any(ProductSearchCriteria.class), any(Pageable.class)))
                .thenReturn(new PageResponse<>(List.of(sampleProduct()), 0, 10, 1, 1, true, true));

        mockMvc.perform(get("/api/v1/products").param("q", "yoga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Yoga Mat"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void unknownProductGives404ProblemDetail() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product with id 99 was not found"));
    }

    @Test
    void creatingWithoutTokenGives401() throws Exception {
        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotCreateProducts() throws Exception {
        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"));
        verifyNoInteractions(productService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCreatesProduct() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(sampleProduct());

        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(VALID_PRODUCT_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/products/42")))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.inStock").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidBodyGives400WithFieldErrors() throws Exception {
        String invalid = """
                { "sku": "bad sku!", "name": "", "price": -5, "stockQuantity": -1 }
                """;

        mockMvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stockQuantity").exists())
                .andExpect(jsonPath("$.errors.categoryId").exists());
        verifyNoInteractions(productService);
    }

    private static ProductResponse sampleProduct() {
        return new ProductResponse(42L, "SPORT-001", "Yoga Mat", "6 mm thick", new BigDecimal("1499.00"),
                25, true, null, 1L, "Sports", Instant.now(), Instant.now());
    }
}

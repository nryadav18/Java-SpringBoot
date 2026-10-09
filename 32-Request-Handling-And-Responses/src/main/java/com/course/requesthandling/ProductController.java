package com.course.requesthandling;

import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/*
 * Full CRUD for /api/products, returning ResponseEntity so WE control
 * the status code, the headers AND the body of every response.
 *
 *   Method  URL                         Meaning            Success     Failure
 *   GET     /api/products               list (+filters)    200         -
 *   GET     /api/products/{id}          one product        200         404
 *   POST    /api/products               create             201 + Location header
 *   PUT     /api/products/{id}          replace all fields 200         404
 *   PATCH   /api/products/{id}          change some fields 200         404
 *   DELETE  /api/products/{id}          delete             204         404
 *   GET     /api/products/headers-demo  @RequestHeader demo
 *
 * produces = JSON: this controller only speaks JSON. A client sending
 * "Accept: application/xml" gets 406 Not Acceptable (content negotiation).
 */
@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ------------------------------------------------------------------ READ (list)
    // GET /api/products
    // GET /api/products?category=electronics
    // GET /api/products?category=electronics&maxPrice=1000&sort=price
    //
    // @RequestParam(required = false)  -> parameter may be missing (value is null)
    // @RequestParam(defaultValue = ..) -> used when missing (and makes it optional automatically)
    @GetMapping
    public ResponseEntity<List<Product>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(name = "sort", defaultValue = "id") String sortBy) {

        List<Product> result = productService.findAll(category, maxPrice, sortBy);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))   // a custom response header
                .body(result);
    }

    // ------------------------------------------------------------------ READ (one)
    // GET /api/products/1  -> 200 + product,  GET /api/products/999 -> 404 (empty body)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)                       // found     -> 200 OK + body
                .orElse(ResponseEntity.notFound().build());    // not found -> 404, no body
    }

    // ------------------------------------------------------------------ CREATE
    // POST /api/products  with JSON body  ->  201 Created
    //   + header  Location: http://localhost:8080/api/products/5   (where the new product lives)
    //   + the created product in the body
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> create(
            @RequestBody ProductRequest request,
            @RequestHeader(name = "X-Client-Id", required = false) String clientId) {

        Product created = productService.create(request);

        // Build the URL of the new resource from the CURRENT request URL:
        //   http://localhost:8080/api/products  +  /{id}  ->  .../api/products/5
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location)                // 201 + Location header
                .header("X-Created-By", clientId == null ? "anonymous" : clientId)
                .body(created);
    }

    // ------------------------------------------------------------------ UPDATE (full)
    // PUT /api/products/1 with the COMPLETE product JSON. Fields you leave out become empty!
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> replace(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.replace(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------------ UPDATE (partial)
    // PATCH /api/products/1 with ONLY the fields to change, e.g. {"price": 699}
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> patch(@PathVariable Long id, @RequestBody ProductPatchRequest patch) {
        return productService.patch(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------------ DELETE
    // DELETE /api/products/1 -> 204 No Content (done, nothing to send back) or 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ------------------------------------------------------------------ HEADERS demo
    // @RequestHeader reads HTTP request headers. Here we read one by name and ALL of them.
    // @CrossOrigin on a single method: browsers on ANY website may call this one endpoint.
    @CrossOrigin(origins = "*")
    @GetMapping("/headers-demo")
    public ResponseEntity<Map<String, Object>> headersDemo(
            @RequestHeader(name = HttpHeaders.USER_AGENT, defaultValue = "unknown") String userAgent,
            @RequestHeader(name = "Accept-Language", required = false) String language,
            @RequestHeader HttpHeaders allHeaders) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userAgent", userAgent);
        body.put("acceptLanguage", language);
        body.put("headerNames", allHeaders.keySet());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())   // "do not cache this answer"
                .body(body);
    }
}

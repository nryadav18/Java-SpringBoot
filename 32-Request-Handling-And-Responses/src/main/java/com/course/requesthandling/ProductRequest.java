package com.course.requesthandling;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * The JSON body a client sends for POST (create) and PUT (full replace).
 *
 *   {"name":"Wireless Mouse","category":"electronics","price":799.00,"stock":50}
 *
 * Notice: NO id (the server decides it) and NO supplierCode / timestamps (server-side details).
 * Keeping "what the client may send" separate from the Product class protects us from
 * clients setting fields they should not touch. (Topic 33 calls this a DTO.)
 */
public record ProductRequest(
        String name,
        String category,
        BigDecimal price,
        @JsonProperty("stock") int quantityInStock) {
}

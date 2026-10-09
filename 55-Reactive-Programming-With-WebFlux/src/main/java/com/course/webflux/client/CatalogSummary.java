package com.course.webflux.client;

import java.math.BigDecimal;

/* Result of the WebClient demo: a small summary built from another API's data. */
public record CatalogSummary(long productCount, BigDecimal totalValue, String source) {
}

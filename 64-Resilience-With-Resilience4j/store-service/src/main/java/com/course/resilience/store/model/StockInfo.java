/*
 * StockInfo - stock data as the store shows it.
 *   source = "inventory-service"      -> fresh, real data
 *   source = "fallback-last-known"    -> remembered value from an earlier successful call
 *   source = "fallback-unknown"       -> we know nothing; quantity -1 means "unknown"
 * The "note" explains WHY a fallback was used (handy while learning).
 */
package com.course.resilience.store.model;

public record StockInfo(String sku, int quantity, String source, String note) {

    public StockInfo withSourceAndNote(String newSource, String newNote) {
        return new StockInfo(sku, quantity, newSource, newNote);
    }
}

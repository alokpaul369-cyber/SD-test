package com.medix.common.enums;

/**
 * Sort order for availability search results.
 */
public enum SearchSort {
    /** Nearest pharmacy first (default). */
    DISTANCE,
    /** Cheapest unit price first. */
    PRICE,
    /** Largest available quantity first. */
    QUANTITY
}

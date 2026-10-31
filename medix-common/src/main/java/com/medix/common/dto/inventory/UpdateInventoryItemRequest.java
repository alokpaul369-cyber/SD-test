package com.medix.common.dto.inventory;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Change price and low-stock threshold of a batch. Quantities are changed only via add/adjust stock.
 *
 * @param version version from the InventoryItemResponse being edited (optimistic locking)
 */
public record UpdateInventoryItemRequest(
        @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal price,
        @Min(0) @Max(10_000) int lowStockThreshold,
        long version
) {
}

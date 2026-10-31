package com.medix.common.dto.inventory;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Receive stock. If the pharmacy already has a batch with the same medicine and batchNumber,
 * its quantity is increased (and price/threshold updated); otherwise a new batch is created.
 * Recorded as a STOCK_IN movement.
 *
 * @param lowStockThreshold optional; defaults to 10 for a new batch, unchanged for an existing one
 */
public record AddStockRequest(
        @NotNull Long medicineId,
        @NotBlank @Size(max = 50) String batchNumber,
        @NotNull @Future LocalDate expiryDate,
        @Positive @Max(100_000) int quantity,
        @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal price,
        @Min(0) @Max(10_000) Integer lowStockThreshold
) {
}

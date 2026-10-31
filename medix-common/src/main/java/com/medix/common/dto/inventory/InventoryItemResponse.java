package com.medix.common.dto.inventory;

import com.medix.common.dto.medicine.MedicineSuggestion;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * One stock batch of a medicine at a pharmacy. A pharmacy can hold several batches of the
 * same medicine (different batch numbers / expiry dates).
 *
 * @param quantity          units on hand
 * @param reservedQuantity  units held for active reservations
 * @param availableQuantity quantity - reservedQuantity (0 when expired)
 * @param lowStockThreshold low-stock alert fires when availableQuantity &lt;= this
 * @param lowStock          availableQuantity &lt;= lowStockThreshold
 * @param expiringSoon      expires within the warning window (30 days by default)
 * @param expired           expiryDate is before today
 * @param version           optimistic-lock version; send back unchanged on updates
 */
public record InventoryItemResponse(
        Long id,
        Long pharmacyId,
        MedicineSuggestion medicine,
        String batchNumber,
        LocalDate expiryDate,
        int quantity,
        int reservedQuantity,
        int availableQuantity,
        BigDecimal price,
        int lowStockThreshold,
        boolean lowStock,
        boolean expiringSoon,
        boolean expired,
        long version,
        Instant updatedAt
) {
}

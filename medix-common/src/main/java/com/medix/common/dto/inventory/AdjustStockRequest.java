package com.medix.common.dto.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medix.common.enums.MovementType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Manual stock change on one batch.
 *
 * @param quantityChange non-zero; negative for SALE and EXPIRED_REMOVAL, either sign for ADJUSTMENT
 * @param type           SALE, ADJUSTMENT or EXPIRED_REMOVAL
 * @param reason         free-text reason stored on the stock movement
 * @param version        version from the InventoryItemResponse being edited (optimistic locking)
 */
public record AdjustStockRequest(
        @Min(-100_000) @Max(100_000) int quantityChange,
        @NotNull MovementType type,
        @NotBlank @Size(max = 255) String reason,
        long version
) {

    @JsonIgnore
    @AssertTrue(message = "type must be SALE, ADJUSTMENT or EXPIRED_REMOVAL")
    public boolean isManualType() {
        return type == null || type.isManual();
    }

    @JsonIgnore
    @AssertTrue(message = "quantityChange must be non-zero, and negative for SALE and EXPIRED_REMOVAL")
    public boolean isQuantitySignValid() {
        if (quantityChange == 0) {
            return false;
        }
        boolean mustBeNegative = type == MovementType.SALE || type == MovementType.EXPIRED_REMOVAL;
        return !mustBeNegative || quantityChange < 0;
    }
}

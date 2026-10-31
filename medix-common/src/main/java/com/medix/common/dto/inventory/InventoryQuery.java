package com.medix.common.dto.inventory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Query parameters of GET /api/pharmacies/{pharmacyId}/inventory. All filters are optional.
 *
 * @param q                  medicine name / generic name / batch number contains
 * @param lowStockOnly       only items with lowStock = true
 * @param expiringWithinDays only items expiring within this many days (expired items included)
 * @param page               0-based page (default 0)
 * @param size               page size 1-100 (default 20)
 */
public record InventoryQuery(
        @Size(max = 100) String q,
        Boolean lowStockOnly,
        @Min(0) @Max(365) Integer expiringWithinDays,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {

    public InventoryQuery {
        lowStockOnly = lowStockOnly != null && lowStockOnly;
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

    public static InventoryQuery all(int page, int size) {
        return new InventoryQuery(null, null, null, page, size);
    }
}

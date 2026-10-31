package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.inventory.AddStockRequest;
import com.medix.common.dto.inventory.AdjustStockRequest;
import com.medix.common.dto.inventory.CsvImportReport;
import com.medix.common.dto.inventory.InventoryItemResponse;
import com.medix.common.dto.inventory.InventoryQuery;
import com.medix.common.dto.inventory.StockMovementResponse;
import com.medix.common.dto.inventory.UpdateInventoryItemRequest;

import java.nio.file.Path;

/**
 * Stock batches of one pharmacy. "Owner" below means the PHARMACY_OWNER who owns {@code pharmacyId};
 * other owners get FORBIDDEN.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface InventoryService {

    /**
     * Lists stock batches with optional filters.
     * <p>{@code GET /api/pharmacies/{pharmacyId}/inventory?q=&lowStockOnly=&expiringWithinDays=&page=&size=} – PHARMACY_OWNER (own), ADMIN
     */
    PageResponse<InventoryItemResponse> list(long pharmacyId, InventoryQuery query);

    /**
     * Returns one stock batch.
     * <p>{@code GET /api/pharmacies/{pharmacyId}/inventory/{itemId}} – PHARMACY_OWNER (own), ADMIN
     */
    InventoryItemResponse get(long pharmacyId, long itemId);

    /**
     * Receives stock: creates a batch or tops up the batch with the same medicine and batch number.
     * <p>{@code POST /api/pharmacies/{pharmacyId}/inventory} – PHARMACY_OWNER (own)
     */
    InventoryItemResponse addStock(long pharmacyId, AddStockRequest request);

    /**
     * Records a sale, correction or expired-stock removal on one batch.
     * <p>{@code POST /api/pharmacies/{pharmacyId}/inventory/{itemId}/adjust} – PHARMACY_OWNER (own)
     */
    InventoryItemResponse adjustStock(long pharmacyId, long itemId, AdjustStockRequest request);

    /**
     * Changes price and low-stock threshold of a batch.
     * <p>{@code PATCH /api/pharmacies/{pharmacyId}/inventory/{itemId}} – PHARMACY_OWNER (own)
     */
    InventoryItemResponse update(long pharmacyId, long itemId, UpdateInventoryItemRequest request);

    /**
     * Deletes a batch that has no reserved quantity (else INSUFFICIENT_STOCK).
     * <p>{@code DELETE /api/pharmacies/{pharmacyId}/inventory/{itemId}} – PHARMACY_OWNER (own)
     */
    void delete(long pharmacyId, long itemId);

    /**
     * Imports stock from a CSV file (max 2 MB, max 5000 rows). Valid rows are applied; invalid rows are reported.
     * <p>{@code POST /api/pharmacies/{pharmacyId}/inventory/import} (multipart/form-data, part "file") – PHARMACY_OWNER (own)
     */
    CsvImportReport importCsv(long pharmacyId, Path csvFile);

    /**
     * Stock movement history, newest first.
     * <p>{@code GET /api/pharmacies/{pharmacyId}/inventory/movements?itemId=&page=&size=} – PHARMACY_OWNER (own), ADMIN
     *
     * @param itemId only movements of this batch; null for all batches of the pharmacy
     */
    PageResponse<StockMovementResponse> movements(long pharmacyId, Long itemId, int page, int size);
}

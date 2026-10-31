package com.medix.common.dto.inventory;

import java.util.List;

/**
 * Result of an inventory CSV import. Valid rows are imported even if other rows fail.
 * <p>
 * Expected CSV header (comma separated, UTF-8):
 * {@code medicine_name,strength,dosage_form,batch_number,expiry_date,quantity,price,low_stock_threshold}
 * with expiry_date as yyyy-MM-dd and low_stock_threshold optional.
 *
 * @param totalRows    data rows read (header excluded)
 * @param importedRows rows applied as CSV_IMPORT movements
 * @param failedRows   rows rejected
 * @param errors       one entry per problem, in row order
 */
public record CsvImportReport(
        int totalRows,
        int importedRows,
        int failedRows,
        List<CsvRowError> errors
) {

    public CsvImportReport {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public boolean hasErrors() {
        return failedRows > 0;
    }

    /**
     * @param rowNumber 1-based line number in the file (the header is line 1)
     * @param column    CSV column name, or null for whole-row problems
     * @param message   what is wrong
     * @param rawLine   the original line, for display
     */
    public record CsvRowError(int rowNumber, String column, String message, String rawLine) {
    }
}

package com.medix.common.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Query parameters of GET /api/admin/audit-log. All filters are optional; dates are inclusive.
 */
public record AuditLogQuery(
        @Size(max = 50) String action,
        @Size(max = 50) String entityType,
        Long actorId,
        LocalDate from,
        LocalDate to,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {

    public AuditLogQuery {
        page = page == null ? 0 : page;
        size = size == null ? 50 : size;
    }

    public static AuditLogQuery all(int page, int size) {
        return new AuditLogQuery(null, null, null, null, null, page, size);
    }
}

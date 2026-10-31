package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.admin.AdminUserQuery;
import com.medix.common.dto.admin.AdminUserRow;
import com.medix.common.dto.admin.AuditLogQuery;
import com.medix.common.dto.admin.AuditLogRow;
import com.medix.common.dto.admin.DashboardStatsResponse;
import com.medix.common.dto.admin.UserStatusUpdateRequest;
import com.medix.common.dto.pharmacy.PharmacyResponse;
import com.medix.common.dto.pharmacy.PharmacyStatusUpdateRequest;
import com.medix.common.enums.PharmacyStatus;

/**
 * Administration. Every method requires role ADMIN.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface AdminService {

    /**
     * Dashboard numbers and chart data.
     * <p>{@code GET /api/admin/dashboard} – ADMIN
     */
    DashboardStatsResponse dashboard();

    /**
     * Lists users with optional filters.
     * <p>{@code GET /api/admin/users?q=&role=&enabled=&page=&size=} – ADMIN
     */
    PageResponse<AdminUserRow> users(AdminUserQuery query);

    /**
     * Enables or disables a user. Admins cannot disable themselves (FORBIDDEN).
     * <p>{@code PATCH /api/admin/users/{id}/status} – ADMIN
     */
    AdminUserRow updateUserStatus(long id, UserStatusUpdateRequest request);

    /**
     * Lists pharmacies of any status, PENDING first then newest.
     * <p>{@code GET /api/admin/pharmacies?status=&q=&page=&size=} – ADMIN
     *
     * @param status only this status; null for all
     * @param q      name, license number or city contains; null for all
     */
    PageResponse<PharmacyResponse> pharmacies(PharmacyStatus status, String q, int page, int size);

    /**
     * Approves, rejects or suspends a pharmacy and notifies the owner.
     * <p>{@code PATCH /api/admin/pharmacies/{id}/status} – ADMIN
     */
    PharmacyResponse updatePharmacyStatus(long id, PharmacyStatusUpdateRequest request);

    /**
     * Audit log, newest first.
     * <p>{@code GET /api/admin/audit-log?action=&entityType=&actorId=&from=&to=&page=&size=} – ADMIN
     */
    PageResponse<AuditLogRow> auditLog(AuditLogQuery query);
}

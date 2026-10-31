package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.alert.AlertResponse;

/**
 * In-app alerts for the logged-in user (low stock, expiry, reservation updates, pharmacy reviews).
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface AlertService {

    /**
     * The current user's alerts, newest first.
     * <p>{@code GET /api/alerts?unreadOnly=&page=&size=} – authenticated (any role)
     */
    PageResponse<AlertResponse> list(boolean unreadOnly, int page, int size);

    /**
     * Number of unread alerts (polled by the client for the badge).
     * <p>{@code GET /api/alerts/unread-count} – authenticated (any role)
     */
    long unreadCount();

    /**
     * Marks one of the user's alerts as read.
     * <p>{@code POST /api/alerts/{id}/read} – authenticated (own alert)
     */
    void markRead(long id);

    /**
     * Marks all of the user's alerts as read.
     * <p>{@code POST /api/alerts/read-all} – authenticated (any role)
     */
    void markAllRead();
}

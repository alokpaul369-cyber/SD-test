package com.medix.client.api.service;

import com.medix.common.dto.PageResponse;
import com.medix.common.dto.reservation.CancelReservationRequest;
import com.medix.common.dto.reservation.CreateReservationRequest;
import com.medix.common.dto.reservation.PickupVerificationRequest;
import com.medix.common.dto.reservation.ReservationResponse;
import com.medix.common.dto.reservation.ReservationStatusUpdateRequest;
import com.medix.common.enums.ReservationStatus;

/**
 * Medicine reservations.
 * <p>
 * All methods block and throw {@link ApiException} on failure: call them from a SwingWorker, never on the EDT.
 */
public interface ReservationService {

    /**
     * Reserves medicines at one pharmacy and holds the stock. Returns the pickup code and expiry time.
     * <p>{@code POST /api/reservations} – CUSTOMER
     */
    ReservationResponse create(CreateReservationRequest request);

    /**
     * The current customer's reservations, newest first.
     * <p>{@code GET /api/reservations/mine?status=&page=&size=} – CUSTOMER
     *
     * @param status only this status; null for all
     */
    PageResponse<ReservationResponse> myReservations(ReservationStatus status, int page, int size);

    /**
     * Returns one reservation.
     * <p>{@code GET /api/reservations/{id}} – CUSTOMER (own), PHARMACY_OWNER (reservation at own pharmacy), ADMIN
     */
    ReservationResponse get(long id);

    /**
     * Cancels the customer's own active reservation and releases the stock.
     * <p>{@code POST /api/reservations/{id}/cancel} – CUSTOMER (own)
     */
    ReservationResponse cancel(long id, CancelReservationRequest request);

    /**
     * Reservations at one pharmacy, newest first.
     * <p>{@code GET /api/pharmacies/{pharmacyId}/reservations?status=&page=&size=} – PHARMACY_OWNER (own), ADMIN
     *
     * @param status only this status; null for all
     */
    PageResponse<ReservationResponse> pharmacyReservations(long pharmacyId, ReservationStatus status, int page, int size);

    /**
     * Moves a reservation at the owner's pharmacy to CONFIRMED, READY_FOR_PICKUP or CANCELLED.
     * <p>{@code PATCH /api/pharmacies/{pharmacyId}/reservations/{id}/status} – PHARMACY_OWNER (own)
     */
    ReservationResponse updateStatus(long pharmacyId, long id, ReservationStatusUpdateRequest request);

    /**
     * Completes the active reservation with this pickup code at the owner's pharmacy and deducts the held stock.
     * <p>{@code POST /api/pharmacies/{pharmacyId}/reservations/complete} – PHARMACY_OWNER (own)
     */
    ReservationResponse completeByPickupCode(long pharmacyId, PickupVerificationRequest request);
}

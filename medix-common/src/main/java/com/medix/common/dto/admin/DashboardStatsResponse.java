package com.medix.common.dto.admin;

import com.medix.common.dto.search.SearchStat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Numbers for the admin dashboard and its charts.
 *
 * @param activeReservations      reservations in PENDING, CONFIRMED or READY_FOR_PICKUP
 * @param lowStockItems           non-expired batches with lowStock = true
 * @param expiringSoonItems       batches expiring within the warning window
 * @param topSearches             top 10 search terms, most searched first
 * @param reservationsLast7Days   one entry per day, oldest first, today included
 * @param searchesLast7Days       one entry per day, oldest first, today included
 * @param generatedAt             when the numbers were computed (may be cached for a minute)
 */
public record DashboardStatsResponse(
        long totalUsers,
        long totalCustomers,
        long totalOwners,
        long totalPharmacies,
        long pendingPharmacies,
        long approvedPharmacies,
        long totalMedicines,
        long activeReservations,
        long reservationsToday,
        long lowStockItems,
        long expiringSoonItems,
        List<SearchStat> topSearches,
        List<DailyCount> reservationsLast7Days,
        List<DailyCount> searchesLast7Days,
        Instant generatedAt
) {

    public DashboardStatsResponse {
        topSearches = topSearches == null ? List.of() : List.copyOf(topSearches);
        reservationsLast7Days = reservationsLast7Days == null ? List.of() : List.copyOf(reservationsLast7Days);
        searchesLast7Days = searchesLast7Days == null ? List.of() : List.copyOf(searchesLast7Days);
    }

    /** A count for one calendar day. */
    public record DailyCount(LocalDate date, long count) {
    }
}

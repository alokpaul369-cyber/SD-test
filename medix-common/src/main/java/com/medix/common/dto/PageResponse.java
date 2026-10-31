package com.medix.common.dto;

import java.util.List;
import java.util.function.Function;

/**
 * One page of results. Pages are 0-based.
 *
 * @param content       items on this page
 * @param page          0-based page index
 * @param size          requested page size
 * @param totalElements total number of items across all pages
 * @param totalPages    total number of pages
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public PageResponse {
        content = content == null ? List.of() : List.copyOf(content);
    }

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size <= 0 ? 0 : (int) ((totalElements + size - 1) / size);
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }

    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(List.of(), page, size, 0, 0);
    }

    /** Pages a full in-memory list. Useful for mock services. */
    public static <T> PageResponse<T> slice(List<T> all, int page, int size) {
        int from = Math.min(Math.max(page, 0) * size, all.size());
        int to = Math.min(from + size, all.size());
        return of(all.subList(from, to), page, size, all.size());
    }

    public <R> PageResponse<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = content.stream().<R>map(mapper).toList();
        return new PageResponse<>(mapped, page, size, totalElements, totalPages);
    }

    public boolean hasNext() {
        return page + 1 < totalPages;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}

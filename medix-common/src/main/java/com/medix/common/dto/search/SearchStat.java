package com.medix.common.dto.search;

/**
 * How often a search term was used (from Redis search statistics).
 *
 * @param term  normalized (lower-case, trimmed) search term
 * @param count number of searches
 */
public record SearchStat(String term, long count) {
}

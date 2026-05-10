package com.dev.petmarket_android.common.model

/**
 * Generic paginated response wrapper.
 * Wraps a list of content items with pagination metadata.
 */
data class PaginatedResponse<T>(
    val content: List<T>,
    val pageInfo: PageInfo
)

/**
 * Pagination metadata for responses.
 */
data class PageInfo(
    val page: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

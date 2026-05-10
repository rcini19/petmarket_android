package com.dev.petmarket_android.common.model

import org.junit.Assert.*
import org.junit.Test

class PaginationModelsTest {

    @Test
    fun pageInfo_hasNextTrue_correctProperties() {
        val pageInfo = PageInfo(
            page = 0,
            pageSize = 20,
            totalElements = 100,
            totalPages = 5,
            hasNext = true,
            hasPrevious = false
        )

        assertEquals(0, pageInfo.page)
        assertEquals(20, pageInfo.pageSize)
        assertEquals(100L, pageInfo.totalElements)
        assertEquals(5, pageInfo.totalPages)
        assertTrue(pageInfo.hasNext)
        assertFalse(pageInfo.hasPrevious)
    }

    @Test
    fun pageInfo_lastPage_correctProperties() {
        val pageInfo = PageInfo(
            page = 4,
            pageSize = 20,
            totalElements = 100,
            totalPages = 5,
            hasNext = false,
            hasPrevious = true
        )

        assertEquals(4, pageInfo.page)
        assertFalse(pageInfo.hasNext)
        assertTrue(pageInfo.hasPrevious)
    }

    @Test
    fun paginatedResponse_containsCorrectData() {
        val pageInfo = PageInfo(
            page = 0,
            pageSize = 20,
            totalElements = 50,
            totalPages = 3,
            hasNext = true,
            hasPrevious = false
        )

        val content = listOf(
            PetResponse(id = 1, name = "Pet1", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Pet2", price = 150.0, status = "AVAILABLE")
        )

        val response = PaginatedResponse(content = content, pageInfo = pageInfo)

        assertEquals(2, response.content.size)
        assertEquals(pageInfo, response.pageInfo)
        assertEquals("Pet1", response.content[0].name)
        assertEquals(1L, response.content[0].id)
    }

    @Test
    fun paginatedResponse_emptyContent_stillValid() {
        val pageInfo = PageInfo(
            page = 0,
            pageSize = 20,
            totalElements = 0,
            totalPages = 0,
            hasNext = false,
            hasPrevious = false
        )

        val response = PaginatedResponse(content = emptyList(), pageInfo = pageInfo)

        assertTrue(response.content.isEmpty())
        assertEquals(0, response.pageInfo.totalElements)
    }

    @Test
    fun paginatedResponse_largeDataset_handlesCorrectly() {
        val content = (1..20).map { id ->
            PetResponse(id = id.toLong(), name = "Pet$id", price = 100.0 * id, status = "AVAILABLE")
        }

        val pageInfo = PageInfo(
            page = 0,
            pageSize = 20,
            totalElements = 1000,
            totalPages = 50,
            hasNext = true,
            hasPrevious = false
        )

        val response = PaginatedResponse(content = content, pageInfo = pageInfo)

        assertEquals(20, response.content.size)
        assertEquals(1000L, response.pageInfo.totalElements)
        assertEquals(50, response.pageInfo.totalPages)
        assertTrue(response.pageInfo.hasNext)
    }

    @Test
    fun pageInfo_calculatesPaginationCorrectly() {
        // Total 105 items, page size 20 = 6 pages (0-5)
        // Page 3 (0-indexed) = items 60-79

        val pageInfo = PageInfo(
            page = 3,
            pageSize = 20,
            totalElements = 105,
            totalPages = 6,
            hasNext = true,
            hasPrevious = true
        )

        assertTrue("Page 3 should have next page", pageInfo.hasNext)
        assertTrue("Page 3 should have previous page", pageInfo.hasPrevious)
    }

    @Test
    fun paginatedResponse_differentiatesByContent() {
        val content1 = listOf(
            PetResponse(id = 1, name = "Pet1", price = 100.0, status = "AVAILABLE")
        )

        val content2 = listOf(
            PetResponse(id = 2, name = "Pet2", price = 150.0, status = "AVAILABLE")
        )

        val pageInfo = PageInfo(
            page = 0,
            pageSize = 20,
            totalElements = 2,
            totalPages = 1,
            hasNext = false,
            hasPrevious = false
        )

        val response1 = PaginatedResponse(content = content1, pageInfo = pageInfo)
        val response2 = PaginatedResponse(content = content2, pageInfo = pageInfo)

        assertNotEquals(response1.content[0].id, response2.content[0].id)
    }
}

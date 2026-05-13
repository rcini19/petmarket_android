package com.dev.petmarket_android.data

import android.content.Context
import com.dev.petmarket_android.common.model.AdminUserResponse
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.PaginatedResponse
import com.dev.petmarket_android.common.network.ApiClient
import com.dev.petmarket_android.common.network.ApiExecutor

class AdminRepository(context: Context) {

    private val api = ApiClient.getService(context)

    // Original method for backward compatibility
    fun getAdminPets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        getAdminPetsWithPagination(0, ADMIN_PAGE_SIZE, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    // New paginated method
    fun getAdminPetsWithPagination(
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/admin/pets", "/admin/pets"),
            callFactory = { endpoint -> api.getAdminPets(endpoint, page, pageSize) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteAdminPet(
        petId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeStatusWithFallback(
            endpoints = listOf("/api/admin/pets/$petId", "/admin/pets/$petId"),
            callFactory = { endpoint -> api.deleteAdminPet(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    // Original method for backward compatibility
    fun getAdminUsers(
        onSuccess: (List<AdminUserResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        getAdminUsersWithPagination(0, ADMIN_PAGE_SIZE, { response ->
            onSuccess(response.content)
        }, onFailure)
    }

    // New paginated method
    fun getAdminUsersWithPagination(
        page: Int,
        pageSize: Int,
        onSuccess: (PaginatedResponse<AdminUserResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeWithFallback(
            endpoints = listOf("/api/admin/users", "/admin/users"),
            callFactory = { endpoint -> api.getAdminUsers(endpoint, page, pageSize) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun suspendAdminUser(
        userId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiExecutor.executeStatusWithFallback(
            endpoints = listOf("/api/admin/users/$userId/suspend", "/admin/users/$userId/suspend"),
            callFactory = { endpoint -> api.suspendAdminUser(endpoint) },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private companion object {
        const val ADMIN_PAGE_SIZE = 100
    }
}

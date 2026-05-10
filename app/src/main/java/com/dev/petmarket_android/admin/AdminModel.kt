package com.dev.petmarket_android.admin

import com.dev.petmarket_android.common.model.AdminUserResponse
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.data.AdminRepository

class AdminModel(private val repository: AdminRepository) {

    fun loadData(
        onSuccess: (List<PetResponse>, List<AdminUserResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var pets: List<PetResponse> = emptyList()
        var users: List<AdminUserResponse> = emptyList()
        var completed = 0
        var failed = false

        fun completeOne() {
            completed += 1
            if (!failed && completed == 2) {
                onSuccess(pets, users)
            }
        }

        repository.getAdminPets(
            onSuccess = {
                pets = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )

        repository.getAdminUsers(
            onSuccess = {
                users = it
                completeOne()
            },
            onFailure = {
                if (!failed) {
                    failed = true
                    onFailure(it)
                }
            }
        )
    }

    fun deletePet(petId: Long, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.deleteAdminPet(petId, onSuccess, onFailure)
    }

    fun suspendUser(userId: Long, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        repository.suspendAdminUser(userId, onSuccess, onFailure)
    }
}

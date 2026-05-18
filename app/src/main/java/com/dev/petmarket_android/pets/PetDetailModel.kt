package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.data.PetRepository

class PetDetailModel(private val repository: PetRepository) {

    fun loadPet(
        petId: Long,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getPetById(petId, onSuccess, onFailure)
    }

    fun createOrder(
        petId: Long,
        totalPrice: Double,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.purchasePet(petId, totalPrice, onSuccess, onFailure)
    }
}

package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.OrderResponse
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
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.createOrder(petId, totalPrice, onSuccess, onFailure)
    }
}

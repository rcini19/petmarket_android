package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetRequest
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.data.PetRepository

class ListingFormModel(private val repository: PetRepository) {

    fun getPetById(
        petId: Long,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getPetById(petId, onSuccess, onFailure)
    }

    fun createPet(
        payload: PetRequest,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.createPet(payload, onSuccess, onFailure)
    }

    fun updatePet(
        petId: Long,
        payload: PetRequest,
        onSuccess: (PetResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.updatePet(petId, payload, onSuccess, onFailure)
    }
}

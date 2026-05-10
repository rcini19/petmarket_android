package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.data.PetRepository

class MyPetsModel(private val repository: PetRepository) {

    fun loadMyPets(
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getMyPets(onSuccess, onFailure)
    }

    fun deletePet(
        petId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.deletePet(petId, onSuccess, onFailure)
    }
}

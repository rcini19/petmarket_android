package com.dev.petmarket_android.pets

import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.data.PetRepository

class BrowsePetsModel(private val repository: PetRepository) {

    fun loadPets(
        search: String,
        listingType: String,
        onSuccess: (List<PetResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getPets(search, listingType, onSuccess, onFailure)
    }
}

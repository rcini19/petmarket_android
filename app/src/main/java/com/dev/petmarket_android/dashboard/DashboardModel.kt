package com.dev.petmarket_android.dashboard

import com.dev.petmarket_android.data.DashboardRepository

class DashboardModel(private val repository: DashboardRepository) {

    fun loadCounts(
        onSuccess: (availablePets: Int, pendingTrades: Int, myPets: Int) -> Unit,
        onFailure: (String) -> Unit
    ) {
        var availablePets = 0
        var pendingTrades = 0
        var myPets = 0
        var completed = 0
        var failed = false

        fun completeOne() {
            completed += 1
            if (completed == 3 && !failed) {
                onSuccess(availablePets, pendingTrades, myPets)
            }
        }

        repository.getAvailablePets(
            onSuccess = { pets ->
                availablePets = pets.size
                completeOne()
            },
            onFailure = { error ->
                if (!failed) {
                    failed = true
                    onFailure(error)
                }
            }
        )

        repository.getMyActivePets(
            onSuccess = { pets ->
                myPets = pets.size
                completeOne()
            },
            onFailure = { error ->
                if (!failed) {
                    failed = true
                    onFailure(error)
                }
            }
        )

        repository.getTradeOffers(
            onSuccess = { offers ->
                pendingTrades = offers.count { (it.status ?: "").equals("PENDING", ignoreCase = true) }
                completeOne()
            },
            onFailure = { error ->
                if (!failed) {
                    failed = true
                    onFailure(error)
                }
            }
        )
    }
}

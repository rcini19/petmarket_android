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
        var successes = 0
        var lastError: String? = null

        fun completeOne() {
            completed += 1
            if (completed == 3 && successes > 0) {
                onSuccess(availablePets, pendingTrades, myPets)
            } else if (completed == 3) {
                onFailure(lastError ?: "Unable to load dashboard activity")
            }
        }

        repository.getAvailablePetsCount(
            onSuccess = { count ->
                availablePets = count
                successes += 1
                completeOne()
            },
            onFailure = { error ->
                lastError = error
                completeOne()
            }
        )

        repository.getMyPetsCount(
            onSuccess = { count ->
                myPets = count
                successes += 1
                completeOne()
            },
            onFailure = { error ->
                lastError = error
                completeOne()
            }
        )

        repository.getPendingTradeOffersCount(
            onSuccess = { count ->
                pendingTrades = count
                successes += 1
                completeOne()
            },
            onFailure = { error ->
                lastError = error
                completeOne()
            }
        )
    }
}

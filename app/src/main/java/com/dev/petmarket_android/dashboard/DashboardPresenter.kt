package com.dev.petmarket_android.dashboard

import com.dev.petmarket_android.common.security.JwtUtils
import com.dev.petmarket_android.common.storage.SessionManager

class DashboardPresenter(
    private var view: DashboardContract.View?,
    private val model: DashboardModel,
    private val sessionManager: SessionManager
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        val fullName = sessionManager.getFullName().orEmpty().ifBlank { "PetMarket User" }
        val role = sessionManager.getRole()

        val token = sessionManager.getToken()
        if (!JwtUtils.isTokenUsable(token)) {
            sessionManager.clearSession()
            view?.navigateToLogin()
            return
        }

        view?.showHeader(fullName, role)
        view?.showLoading(true)

        model.loadCounts(
            onSuccess = { availablePets, pendingTrades, myPets ->
                view?.showLoading(false)
                view?.showStats(availablePets, pendingTrades, myPets)
            },
            onFailure = { error ->
                view?.showLoading(false)
                view?.showError(error)
                view?.showStats(0, 0, 0)
            }
        )
    }

    override fun onBrowsePetsClicked() {
        view?.navigateToBrowsePets()
    }

    override fun onCreateListingClicked() {
        view?.navigateToCreateListing()
    }

    override fun onTradeOffersClicked() {
        view?.navigateToTrades()
    }

    override fun onMyPetsClicked() {
        view?.navigateToMyPets()
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onAdminPanelClicked() {
        view?.navigateToAdminPanel()
    }

    override fun onLogoutClicked() {
        sessionManager.clearSession()
        view?.navigateToLogin()
    }

    override fun onDestroy() {
        view = null
    }
}

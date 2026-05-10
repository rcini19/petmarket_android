package com.dev.petmarket_android.dashboard

interface DashboardContract {

    interface View {
        fun showLoading(isLoading: Boolean)
        fun showError(message: String)
        fun showHeader(fullName: String, role: String)
        fun showStats(availablePets: Int, pendingTrades: Int, myPets: Int)
        fun showFeatureMessage(message: String)
        fun navigateToBrowsePets()
        fun navigateToCreateListing()
        fun navigateToMyPets()
        fun navigateToTrades()
        fun navigateToProfile()
        fun navigateToAdminPanel()
        fun navigateToLogin()
    }

    interface Presenter {
        fun loadDashboard()
        fun onBrowsePetsClicked()
        fun onCreateListingClicked()
        fun onTradeOffersClicked()
        fun onMyPetsClicked()
        fun onProfileClicked()
        fun onAdminPanelClicked()
        fun onLogoutClicked()
        fun onDestroy()
    }
}

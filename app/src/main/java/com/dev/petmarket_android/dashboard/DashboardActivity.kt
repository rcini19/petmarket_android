package com.dev.petmarket_android.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.petmarket_android.MainActivity
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.data.DashboardRepository
import com.dev.petmarket_android.admin.AdminActivity
import com.dev.petmarket_android.pets.BrowsePetsActivity
import com.dev.petmarket_android.pets.ListingFormActivity
import com.dev.petmarket_android.pets.MyPetsActivity
import com.dev.petmarket_android.profile.ProfileActivity
import com.dev.petmarket_android.trades.TradesActivity
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.databinding.ActivityDashboardBinding

class DashboardActivity : BaseBottomNavActivity<ActivityDashboardBinding>(), DashboardContract.View {

    private lateinit var presenter: DashboardContract.Presenter

    override fun createBinding() = ActivityDashboardBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        val sessionManager = SessionManager(applicationContext)
        val repository = DashboardRepository(applicationContext)
        val model = DashboardModel(repository)
        presenter = DashboardPresenter(this, model, sessionManager)

        binding.btnBrowsePets.setOnClickListener { presenter.onBrowsePetsClicked() }
        binding.btnCreateListing.setOnClickListener { presenter.onCreateListingClicked() }
        binding.btnTradeOffers.setOnClickListener { presenter.onTradeOffersClicked() }
        binding.btnMyPets.setOnClickListener { presenter.onMyPetsClicked() }
        binding.btnProfile.setOnClickListener { presenter.onProfileClicked() }
        binding.btnAdminPanel.setOnClickListener { presenter.onAdminPanelClicked() }
        binding.btnLogout.setOnClickListener { presenter.onLogoutClicked() }
    }

    override fun getCurrentNavItemId(): Int = R.id.nav_dashboard

    override fun onResume() {
        super.onResume()
        presenter.loadDashboard()
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showHeader(fullName: String, role: String) {
        binding.tvGreeting.text = getString(R.string.dashboard_overview_title)
        binding.tvRole.text = getString(R.string.dashboard_overview_desc)

        val isAdmin = role.equals("ADMIN", ignoreCase = true)
        binding.btnAdminPanel.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.tvAdminHint.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    override fun showStats(availablePets: Int, pendingTrades: Int, myPets: Int) {
        binding.tvAvailablePetsValue.text = availablePets.toString()
        binding.tvPendingTradesValue.text = pendingTrades.toString()
        binding.tvMyPetsValue.text = myPets.toString()
    }

    override fun showFeatureMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToBrowsePets() {
        startActivity(Intent(this, BrowsePetsActivity::class.java))
    }

    override fun navigateToCreateListing() {
        startActivity(Intent(this, ListingFormActivity::class.java))
    }

    override fun navigateToMyPets() {
        startActivity(Intent(this, MyPetsActivity::class.java))
    }

    override fun navigateToTrades() {
        startActivity(Intent(this, TradesActivity::class.java))
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToAdminPanel() {
        startActivity(Intent(this, AdminActivity::class.java))
    }

    override fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

package com.dev.petmarket_android.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.AdminUserResponse
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.ui.MarketHeader
import com.dev.petmarket_android.data.AdminRepository

class AdminActivity : AppCompatActivity(), AdminContract.View {

    private lateinit var presenter: AdminContract.Presenter
    private lateinit var petAdapter: AdminPetAdapter
    private lateinit var userAdapter: AdminUserAdapter

    private lateinit var btnListingsTab: Button
    private lateinit var btnUsersTab: Button
    private lateinit var listingsStatsPanel: View
    private lateinit var usersStatsPanel: View
    private lateinit var etUserSearch: EditText
    private lateinit var listingsTableHeader: View
    private lateinit var usersTableHeader: View
    private lateinit var rvPetsContainer: View
    private lateinit var rvUsersContainer: View
    private lateinit var rvPets: RecyclerView
    private lateinit var rvUsers: RecyclerView
    private lateinit var tvPetsEmpty: TextView
    private lateinit var tvUsersEmpty: TextView
    private lateinit var tvTotalListingsValue: TextView
    private lateinit var tvListingsRevenueValue: TextView
    private lateinit var tvTotalUsersValue: TextView
    private lateinit var tvActiveUsersValue: TextView
    private lateinit var tvSuspendedUsersValue: TextView
    private lateinit var tvUsersRevenueValue: TextView
    private lateinit var progressBar: ProgressBar
    private var activeTab = TAB_LISTINGS
    private var currentPets: List<PetResponse> = emptyList()
    private var allUsers: List<AdminUserResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val role = SessionManager(applicationContext).getRole()
        if (!role.equals("ADMIN", ignoreCase = true)) {
            Toast.makeText(this, getString(R.string.admin_access_required), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        MarketHeader.setup(this, R.id.nav_admin)
        bindViews()

        val repository = AdminRepository(applicationContext)
        val model = AdminModel(repository)
        presenter = AdminPresenter(this, model)

        petAdapter = AdminPetAdapter { petId ->
            showDeletePetConfirmation(petId)
        }
        userAdapter = AdminUserAdapter { userId ->
            showSuspendUserConfirmation(userId)
        }

        rvPets.layoutManager = LinearLayoutManager(this)
        rvPets.adapter = petAdapter

        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = userAdapter

        btnListingsTab.setOnClickListener { showListingsTab() }
        btnUsersTab.setOnClickListener { showUsersTab() }
        etUserSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyUserFilter()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        showListingsTab()
        presenter.loadData()
    }

    private fun bindViews() {
        btnListingsTab = findViewById(R.id.btnListingsTab)
        btnUsersTab = findViewById(R.id.btnUsersTab)
        listingsStatsPanel = findViewById(R.id.listingsStatsPanel)
        usersStatsPanel = findViewById(R.id.usersStatsPanel)
        etUserSearch = findViewById(R.id.etUserSearch)
        listingsTableHeader = findViewById(R.id.listingsTableHeader)
        usersTableHeader = findViewById(R.id.usersTableHeader)
        rvPetsContainer = findViewById(R.id.rvPetsContainer)
        rvUsersContainer = findViewById(R.id.rvUsersContainer)
        rvPets = findViewById(R.id.rvPets)
        rvUsers = findViewById(R.id.rvUsers)
        tvPetsEmpty = findViewById(R.id.tvPetsEmpty)
        tvUsersEmpty = findViewById(R.id.tvUsersEmpty)
        tvTotalListingsValue = findViewById(R.id.tvTotalListingsValue)
        tvListingsRevenueValue = findViewById(R.id.tvListingsRevenueValue)
        tvTotalUsersValue = findViewById(R.id.tvTotalUsersValue)
        tvActiveUsersValue = findViewById(R.id.tvActiveUsersValue)
        tvSuspendedUsersValue = findViewById(R.id.tvSuspendedUsersValue)
        tvUsersRevenueValue = findViewById(R.id.tvUsersRevenueValue)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun showListingsTab() {
        activeTab = TAB_LISTINGS
        setTabState()
        listingsStatsPanel.visibility = View.VISIBLE
        listingsTableHeader.visibility = View.VISIBLE
        rvPetsContainer.visibility = View.VISIBLE
        tvPetsEmpty.visibility = if (petAdapter.itemCount == 0) View.VISIBLE else View.GONE
        usersStatsPanel.visibility = View.GONE
        etUserSearch.visibility = View.GONE
        usersTableHeader.visibility = View.GONE
        rvUsersContainer.visibility = View.GONE
        tvUsersEmpty.visibility = View.GONE
    }

    private fun showUsersTab() {
        activeTab = TAB_USERS
        setTabState()
        usersStatsPanel.visibility = View.VISIBLE
        etUserSearch.visibility = View.VISIBLE
        usersTableHeader.visibility = View.VISIBLE
        rvUsersContainer.visibility = View.VISIBLE
        tvUsersEmpty.visibility = if (userAdapter.itemCount == 0) View.VISIBLE else View.GONE
        listingsStatsPanel.visibility = View.GONE
        listingsTableHeader.visibility = View.GONE
        rvPetsContainer.visibility = View.GONE
        tvPetsEmpty.visibility = View.GONE
    }

    private fun setTabState() {
        setTabButtonState(btnListingsTab, activeTab == TAB_LISTINGS)
        setTabButtonState(btnUsersTab, activeTab == TAB_USERS)
    }

    private fun setTabButtonState(button: Button, selected: Boolean) {
        button.setBackgroundResource(
            if (selected) R.drawable.bg_admin_tab_selected else R.drawable.bg_admin_tab_unselected
        )
        button.setTextColor(
            ContextCompat.getColor(this, if (selected) R.color.white else R.color.pm_market_text)
        )
    }

    private fun showDeletePetConfirmation(petId: Long) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete_title))
            .setMessage(getString(R.string.confirm_delete_message, petId.toString()))
            .setPositiveButton(R.string.delete) { _, _ ->
                presenter.onDeletePet(petId)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showSuspendUserConfirmation(userId: Long) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_suspend_title))
            .setMessage(getString(R.string.confirm_suspend_message, userId.toString()))
            .setPositiveButton(R.string.suspend) { _, _ ->
                presenter.onSuspendUser(userId)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showAdminPets(items: List<PetResponse>) {
        currentPets = items
        petAdapter.submitList(items)
        updateListingStats()
        updateRevenueStats()
        if (activeTab == TAB_LISTINGS) {
            tvPetsEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun showAdminUsers(items: List<AdminUserResponse>) {
        allUsers = items
        updateUserStats()
        applyUserFilter()
    }

    private fun applyUserFilter() {
        val query = etUserSearch.text?.toString().orEmpty().trim()
        val filtered = if (query.isBlank()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.fullName.orEmpty().contains(query, ignoreCase = true) ||
                    user.email.orEmpty().contains(query, ignoreCase = true)
            }
        }

        userAdapter.submitList(filtered)
        if (activeTab == TAB_USERS) {
            tvUsersEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateListingStats() {
        tvTotalListingsValue.text = currentPets.size.toString()
    }

    private fun updateUserStats() {
        val suspendedCount = allUsers.count { it.suspended == true }
        tvTotalUsersValue.text = allUsers.size.toString()
        tvActiveUsersValue.text = (allUsers.size - suspendedCount).toString()
        tvSuspendedUsersValue.text = suspendedCount.toString()
    }

    private fun updateRevenueStats() {
        val revenue = currentPets.sumOf { it.price ?: 0.0 }
        val formatted = "$${"%.0f".format(revenue)}"
        tvListingsRevenueValue.text = formatted
        tvUsersRevenueValue.text = formatted
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val TAB_LISTINGS = "listings"
        private const val TAB_USERS = "users"
    }
}

package com.dev.petmarket_android.admin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.AdminUserResponse
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.data.AdminRepository

class AdminActivity : AppCompatActivity(), AdminContract.View {

    private lateinit var presenter: AdminContract.Presenter
    private lateinit var petAdapter: AdminPetAdapter
    private lateinit var userAdapter: AdminUserAdapter

    private lateinit var btnListingsTab: Button
    private lateinit var btnUsersTab: Button
    private lateinit var rvPets: RecyclerView
    private lateinit var rvUsers: RecyclerView
    private lateinit var tvPetsEmpty: TextView
    private lateinit var tvUsersEmpty: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val role = SessionManager(applicationContext).getRole()
        if (!role.equals("ADMIN", ignoreCase = true)) {
            Toast.makeText(this, getString(R.string.admin_access_required), Toast.LENGTH_LONG).show()
            finish()
            return
        }

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

        showListingsTab()
        presenter.loadData()
    }

    private fun bindViews() {
        btnListingsTab = findViewById(R.id.btnListingsTab)
        btnUsersTab = findViewById(R.id.btnUsersTab)
        rvPets = findViewById(R.id.rvPets)
        rvUsers = findViewById(R.id.rvUsers)
        tvPetsEmpty = findViewById(R.id.tvPetsEmpty)
        tvUsersEmpty = findViewById(R.id.tvUsersEmpty)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun showListingsTab() {
        rvPets.visibility = View.VISIBLE
        tvPetsEmpty.visibility = if (petAdapter.itemCount == 0) View.VISIBLE else View.GONE
        rvUsers.visibility = View.GONE
        tvUsersEmpty.visibility = View.GONE
    }

    private fun showUsersTab() {
        rvUsers.visibility = View.VISIBLE
        tvUsersEmpty.visibility = if (userAdapter.itemCount == 0) View.VISIBLE else View.GONE
        rvPets.visibility = View.GONE
        tvPetsEmpty.visibility = View.GONE
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
        petAdapter.submitList(items)
        if (rvPets.visibility == View.VISIBLE) {
            tvPetsEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun showAdminUsers(items: List<AdminUserResponse>) {
        userAdapter.submitList(items)
        if (rvUsers.visibility == View.VISIBLE) {
            tvUsersEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

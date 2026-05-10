package com.dev.petmarket_android.pets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.data.PetRepository
import com.dev.petmarket_android.databinding.ActivityMyPetsBinding

class MyPetsActivity : BaseBottomNavActivity<ActivityMyPetsBinding>(), MyPetsContract.View {

    override fun createBinding() = ActivityMyPetsBinding.inflate(layoutInflater)

    private lateinit var presenter: MyPetsContract.Presenter
    private lateinit var adapter: MyPetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        adapter = MyPetsAdapter(
            onEditClicked = { petId -> presenter.onEditPetClicked(petId) },
            onDeleteClicked = { petId -> presenter.onDeletePetClicked(petId) }
        )
        binding.rvMyPets.layoutManager = LinearLayoutManager(this)
        binding.rvMyPets.adapter = adapter

        val repository = PetRepository(applicationContext)
        val model = MyPetsModel(repository)
        presenter = MyPetsPresenter(this, model)

        binding.btnCreateListing.setOnClickListener { presenter.onCreateListingClicked() }
    }

    override fun getCurrentNavItemId(): Int = R.id.nav_my_pets

    override fun onResume() {
        super.onResume()
        presenter.loadMyPets()
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showPets(items: List<PetResponse>) {
        adapter.submitList(items)
        binding.tvTotalPetsValue.text = items.size.toString()
        binding.tvActivePetsValue.text = items.count {
            it.status.orEmpty().equals("AVAILABLE", ignoreCase = true)
        }.toString()
        binding.tvPendingPetsValue.text = items.count {
            !it.status.orEmpty().equals("AVAILABLE", ignoreCase = true)
        }.toString()
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToCreateListing() {
        startActivity(Intent(this, ListingFormActivity::class.java))
    }

    override fun navigateToEditListing(petId: Long) {
        val intent = Intent(this, ListingFormActivity::class.java)
        intent.putExtra(ListingFormActivity.EXTRA_PET_ID, petId)
        startActivity(intent)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

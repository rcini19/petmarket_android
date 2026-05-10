package com.dev.petmarket_android.pets

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.petmarket_android.databinding.ActivityBrowsePetsBinding
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.data.PetRepository
import com.dev.petmarket_android.R

class BrowsePetsActivity : BaseBottomNavActivity<ActivityBrowsePetsBinding>(), BrowsePetsContract.View {

    override fun createBinding() = ActivityBrowsePetsBinding.inflate(layoutInflater)

    private lateinit var presenter: BrowsePetsContract.Presenter
    private lateinit var adapter: PetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        val repository = PetRepository(applicationContext)
        val model = BrowsePetsModel(repository)
        presenter = BrowsePetsPresenter(this, model)

        setupFilterDropdown()
        setupRecycler()

        binding.btnApplyFilter.setOnClickListener {
            presenter.loadPets(
                search = binding.etSearch.text?.toString().orEmpty(),
                listingType = binding.ddFilterType.text?.toString().orEmpty().ifBlank { "ALL" }
            )
        }

        presenter.loadPets(search = "", listingType = "ALL")
    }

    override fun getCurrentNavItemId(): Int = R.id.nav_browse

    private fun setupFilterDropdown() {
        val options = listOf("ALL", "SALE", "TRADE", "BOTH")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)
        binding.ddFilterType.setAdapter(adapter)
        binding.ddFilterType.setText("ALL", false)
    }

    private fun setupRecycler() {
        adapter = PetAdapter { petId -> presenter.onPetClicked(petId) }
        binding.rvPets.layoutManager = LinearLayoutManager(this)
        binding.rvPets.adapter = adapter
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showPets(items: List<PetResponse>) {
        adapter.submitList(items)
        binding.tvResultCount.text = "${items.size} results"
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateToPetDetail(petId: Long) {
        val intent = Intent(this, PetDetailActivity::class.java)
        intent.putExtra(PetDetailActivity.EXTRA_PET_ID, petId)
        startActivity(intent)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

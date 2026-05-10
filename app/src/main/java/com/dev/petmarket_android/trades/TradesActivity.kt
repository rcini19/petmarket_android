package com.dev.petmarket_android.trades

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.data.TradeRepository
import com.dev.petmarket_android.databinding.ActivityTradesBinding

class TradesActivity : BaseBottomNavActivity<ActivityTradesBinding>(), TradesContract.View {

    override fun createBinding() = ActivityTradesBinding.inflate(layoutInflater)

    private lateinit var presenter: TradesContract.Presenter
    private lateinit var adapter: TradeOfferAdapter

    private var myPets: List<PetResponse> = emptyList()
    private var tradeablePets: List<PetResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        val repository = TradeRepository(applicationContext)
        val model = TradesModel(repository)
        presenter = TradesPresenter(this, model)

        adapter = TradeOfferAdapter(
            onAccept = { presenter.onAcceptTradeOffer(it) },
            onReject = { presenter.onRejectTradeOffer(it) }
        )
        binding.rvTradeOffers.layoutManager = LinearLayoutManager(this)
        binding.rvTradeOffers.adapter = adapter

        binding.btnCreateTrade.setOnClickListener {
            val offered = resolvePetId(binding.ddOfferedPet.text?.toString().orEmpty(), myPets)
            val requested = resolvePetId(binding.ddRequestedPet.text?.toString().orEmpty(), tradeablePets)
            presenter.onCreateTradeOffer(offered, requested)
        }

        presenter.loadData()
    }

    override fun getCurrentNavItemId(): Int = R.id.nav_trades

    private fun resolvePetId(selectedName: String, source: List<PetResponse>): Long? {
        val normalized = selectedName.substringBefore(" (").trim()
        return source.firstOrNull { pet -> pet.name.orEmpty().trim() == normalized }?.id
    }

    private fun setupPetDropdown(
        dropdown: android.widget.AutoCompleteTextView,
        items: List<PetResponse>,
        includeOwner: Boolean
    ) {
        val labels = items.map { pet ->
            val name = pet.name.orEmpty().ifBlank { "Unnamed Pet" }
            if (!includeOwner) {
                name
            } else {
                val owner = pet.ownerName.orEmpty().ifBlank { "Unknown" }
                "$name ($owner)"
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)
        dropdown.setAdapter(adapter)
        dropdown.setText("", false)
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showTradeOffers(items: List<TradeOfferResponse>) {
        adapter.submitList(items)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        binding.tvTotalOffersValue.text = items.size.toString()
        binding.tvPendingOffersValue.text = items.count { it.status.orEmpty().equals("PENDING", ignoreCase = true) }.toString()
    }

    override fun showMyPets(items: List<PetResponse>) {
        myPets = items
        setupPetDropdown(binding.ddOfferedPet, items, includeOwner = false)
    }

    override fun showTradeablePets(items: List<PetResponse>) {
        tradeablePets = items
        setupPetDropdown(binding.ddRequestedPet, items, includeOwner = true)
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

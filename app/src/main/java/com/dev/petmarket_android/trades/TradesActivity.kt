package com.dev.petmarket_android.trades

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.common.util.PetListingRules
import com.dev.petmarket_android.common.util.TradeOfferRules
import com.dev.petmarket_android.common.util.TradeOfferRules.Direction
import com.dev.petmarket_android.data.ProfileRepository
import com.dev.petmarket_android.data.TradeRepository
import com.dev.petmarket_android.databinding.ActivityTradesBinding

class TradesActivity : BaseBottomNavActivity<ActivityTradesBinding>(), TradesContract.View {

    override fun createBinding() = ActivityTradesBinding.inflate(layoutInflater)

    private lateinit var presenter: TradesContract.Presenter
    private lateinit var adapter: TradeOfferAdapter

    private var myPets: List<PetResponse> = emptyList()
    private var tradeablePets: List<PetResponse> = emptyList()
    private var serverTradeOffers: List<TradeOfferResponse> = emptyList()
    private lateinit var sessionManager: SessionManager
    private var profileRefreshAttempted = false
    private var selectedDirection = Direction.INCOMING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        sessionManager = SessionManager(applicationContext)
        val repository = TradeRepository(applicationContext)
        val model = TradesModel(repository)
        presenter = TradesPresenter(this, model)

        adapter = TradeOfferAdapter(
            sessionManager = sessionManager,
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

        binding.tabIncoming.setOnClickListener {
            selectedDirection = Direction.INCOMING
            renderTradeOffers()
        }
        binding.tabOutgoing.setOnClickListener {
            selectedDirection = Direction.OUTGOING
            renderTradeOffers()
        }

    }

    override fun getCurrentNavItemId(): Int = R.id.nav_trades

    override fun onResume() {
        super.onResume()
        loadTradeScreenData()
    }

    private fun loadTradeScreenData() {
        if (!profileRefreshAttempted && sessionManager.getUserId() == null) {
            profileRefreshAttempted = true
            ProfileRepository(applicationContext).getProfile(
                onSuccess = { profile ->
                    sessionManager.updateProfile(
                        email = profile.email,
                        fullName = profile.fullName,
                        role = profile.role,
                        profileImageUrl = profile.resolvedProfileImageUrl,
                        userId = profile.id
                    )
                    presenter.loadData()
                },
                onFailure = {
                    presenter.loadData()
                }
            )
            return
        }

        presenter.loadData()
    }

    private fun resolvePetId(selectedName: String, source: List<PetResponse>): Long? {
        val normalized = selectedName.substringBefore(" (").trim()
        return source.firstOrNull { pet -> pet.name.orEmpty().trim() == normalized }?.id
    }

    private fun setupPetDropdown(
        dropdown: android.widget.AutoCompleteTextView,
        items: List<PetResponse>
    ) {
        val labels = items.map { pet ->
            pet.name.orEmpty().ifBlank { "Unnamed Pet" }
        }
        val adapter = ArrayAdapter(this, R.layout.item_role_dropdown, labels)
        dropdown.setAdapter(adapter)
        dropdown.setText("", false)
        dropdown.threshold = 0
        dropdown.isEnabled = true
        dropdown.isClickable = true
        dropdown.isCursorVisible = false
        dropdown.keyListener = null
        dropdown.setOnClickListener {
            if (labels.isNotEmpty()) {
                dropdown.requestFocus()
                dropdown.post { dropdown.showDropDown() }
            } else {
                Toast.makeText(this, "No available trade pets found", Toast.LENGTH_SHORT).show()
            }
        }
        dropdown.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                dropdown.performClick()
                return@setOnTouchListener true
            }
            false
        }
        dropdown.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && labels.isNotEmpty()) {
                dropdown.post { dropdown.showDropDown() }
            }
        }
    }

    private fun renderTradeOffers() {
        val filteredOffers = serverTradeOffers.filter { trade ->
            TradeOfferRules.direction(trade, sessionManager) == selectedDirection
        }
        adapter.submitList(filteredOffers)
        binding.tvEmpty.visibility = if (filteredOffers.isEmpty()) View.VISIBLE else View.GONE
        binding.tvTotalOffersValue.text = serverTradeOffers.size.toString()
        binding.tvPendingOffersValue.text = serverTradeOffers.count { TradeOfferRules.isPending(it) }.toString()
        binding.tvNeedsResponseValue.text = serverTradeOffers.count { TradeOfferRules.needsResponse(it, sessionManager) }.toString()
        renderTradeTabs()
    }

    private fun renderTradeTabs() {
        val incomingSelected = selectedDirection == Direction.INCOMING
        binding.tabIncoming.setBackgroundResource(if (incomingSelected) R.drawable.bg_icon_chip else R.drawable.bg_nav_pill)
        binding.tabOutgoing.setBackgroundResource(if (incomingSelected) R.drawable.bg_nav_pill else R.drawable.bg_icon_chip)
        binding.tabIncoming.setTextColor(getColor(if (incomingSelected) R.color.pm_market_primary else R.color.nav_pill_text))
        binding.tabOutgoing.setTextColor(getColor(if (incomingSelected) R.color.nav_pill_text else R.color.pm_market_primary))
    }

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showTradeOffers(items: List<TradeOfferResponse>) {
        serverTradeOffers = items
        renderTradeOffers()
    }

    override fun showMyPets(items: List<PetResponse>) {
        myPets = items.filter { pet ->
            PetListingRules.isAvailable(pet) && PetListingRules.supportsTrade(pet)
        }
        setupPetDropdown(binding.ddOfferedPet, myPets)
    }

    override fun showTradeablePets(items: List<PetResponse>) {
        val myPetIds = myPets.map { it.id }.toSet()
        tradeablePets = items.filter { pet ->
            pet.id !in myPetIds &&
                !PetListingRules.isOwnedByCurrentUser(pet, sessionManager) &&
                PetListingRules.isAvailable(pet) &&
                PetListingRules.supportsTrade(pet)
        }
        setupPetDropdown(binding.ddRequestedPet, tradeablePets)
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}

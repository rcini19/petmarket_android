package com.dev.petmarket_android.pets

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.ui.ImageLoader
import com.dev.petmarket_android.common.ui.MarketHeader
import com.dev.petmarket_android.data.PetRepository

class PetDetailActivity : AppCompatActivity(), PetDetailContract.View {

    private lateinit var presenter: PetDetailContract.Presenter

    private lateinit var tvName: TextView
    private lateinit var tvMeta: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvOwner: TextView
    private lateinit var tvStatus: TextView
    private lateinit var ivPetImage: ImageView
    private lateinit var btnPurchase: Button
    private lateinit var progressBar: ProgressBar
    private var currentPet: PetResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)

        MarketHeader.setup(this, R.id.nav_browse)
        bindViews()

        val repository = PetRepository(applicationContext)
        val model = PetDetailModel(repository)
        presenter = PetDetailPresenter(this, model)

        val petId = intent.getLongExtra(EXTRA_PET_ID, -1L)
        if (petId <= 0L) {
            showError("Invalid pet selected")
            finish()
            return
        }

        findViewById<View>(R.id.btnBackToListings).setOnClickListener { finish() }
        btnPurchase.setOnClickListener { showConfirmPurchaseDialog() }

        presenter.loadPet(petId)
    }

    private fun bindViews() {
        tvName = findViewById(R.id.tvName)
        tvMeta = findViewById(R.id.tvMeta)
        tvDescription = findViewById(R.id.tvDescription)
        tvPrice = findViewById(R.id.tvPrice)
        tvOwner = findViewById(R.id.tvOwner)
        tvStatus = findViewById(R.id.tvStatus)
        ivPetImage = findViewById(R.id.ivPetImage)
        btnPurchase = findViewById(R.id.btnPurchase)
        progressBar = findViewById(R.id.progressBar)
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnPurchase.isEnabled = !isLoading
    }

    override fun showPet(pet: PetResponse) {
        currentPet = pet
        tvName.text = pet.name.orEmpty().ifBlank { "Unnamed Pet" }
        tvMeta.text = pet.breed.orEmpty().ifBlank { pet.species.orEmpty() }
        tvDescription.text = pet.description.orEmpty().ifBlank { getString(R.string.no_description) }
        tvPrice.text = pet.price?.let { "$${"%.0f".format(it)}" } ?: "-"
        tvOwner.text = pet.ownerName.orEmpty().ifBlank { "Unknown Owner" }
        tvStatus.text = pet.listingType.orEmpty().ifBlank { "Sale" }.lowercase().replaceFirstChar { it.uppercase() }
        findViewById<TextView>(R.id.tvSpeciesValue).text = pet.species.orEmpty().ifBlank { "-" }
        findViewById<TextView>(R.id.tvAgeValue).text = pet.age?.let { "$it years" } ?: "-"
        findViewById<TextView>(R.id.tvAvailability).text = pet.status.orEmpty()
            .ifBlank { "Available" }
            .lowercase()
            .replaceFirstChar { it.uppercase() }
        ImageLoader.load(ivPetImage, pet.imageUrl)

        val listingType = pet.listingType.orEmpty().uppercase()
        val status = pet.status.orEmpty().uppercase()
        val canPurchase = (listingType == "SALE" || listingType == "BOTH") &&
            status == "AVAILABLE" &&
            (pet.price ?: 0.0) > 0.0
        btnPurchase.isEnabled = canPurchase
        btnPurchase.text = if (!canPurchase) {
            getString(R.string.not_purchasable)
        } else {
            "Purchase - $${"%.0f".format(pet.price ?: 0.0)}"
        }
    }

    private fun showConfirmPurchaseDialog() {
        val pet = currentPet
        if (pet == null) {
            presenter.onPurchaseClicked()
            return
        }

        val price = pet.price ?: 0.0
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_purchase, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tvDialogMessage).text =
            "You're about to purchase ${pet.name.orEmpty().ifBlank { "this pet" }} for $${"%.0f".format(price)}."
        dialogView.findViewById<TextView>(R.id.tvDialogPetName).text = pet.name.orEmpty().ifBlank { "Unnamed Pet" }
        dialogView.findViewById<TextView>(R.id.tvDialogBreed).text = pet.breed.orEmpty().ifBlank { "-" }
        dialogView.findViewById<TextView>(R.id.tvDialogTotal).text = "$${"%.0f".format(price)}"
        dialogView.findViewById<View>(R.id.btnCloseDialog).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<View>(R.id.btnCancelPurchase).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<View>(R.id.btnConfirmPurchase).setOnClickListener {
            dialog.dismiss()
            presenter.onPurchaseClicked()
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showPurchaseSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PET_ID = "extra_pet_id"
    }
}

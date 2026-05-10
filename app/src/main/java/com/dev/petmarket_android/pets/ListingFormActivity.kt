package com.dev.petmarket_android.pets

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.ui.MarketHeader
import com.dev.petmarket_android.data.PetRepository
import com.google.android.material.textfield.TextInputEditText

class ListingFormActivity : AppCompatActivity(), ListingFormContract.View {

    private lateinit var presenter: ListingFormContract.Presenter

    private lateinit var tvTitle: TextView
    private lateinit var etName: TextInputEditText
    private lateinit var etSpecies: TextInputEditText
    private lateinit var etBreed: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var ddListingType: android.widget.AutoCompleteTextView
    private lateinit var etPrice: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etImageUrl: TextInputEditText
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private var editingPetId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_form)

        MarketHeader.setup(this, R.id.nav_my_pets)
        bindViews()
        setupListingTypeDropdown()

        val repository = PetRepository(applicationContext)
        val model = ListingFormModel(repository)
        presenter = ListingFormPresenter(this, model)

        editingPetId = intent.getLongExtra(EXTRA_PET_ID, -1L).takeIf { it > 0L }
        if (editingPetId != null) {
            tvTitle.text = getString(R.string.edit_listing_title)
            btnSubmit.text = getString(R.string.save_changes)
            presenter.loadExistingPet(editingPetId!!)
        } else {
            tvTitle.text = getString(R.string.create_listing_title)
            btnSubmit.text = getString(R.string.create_listing_submit)
        }

        btnSubmit.setOnClickListener {
            presenter.submit(
                petId = editingPetId,
                name = etName.text?.toString().orEmpty(),
                species = etSpecies.text?.toString().orEmpty(),
                breed = etBreed.text?.toString().orEmpty(),
                ageInput = etAge.text?.toString().orEmpty(),
                listingType = ddListingType.text?.toString().orEmpty(),
                priceInput = etPrice.text?.toString().orEmpty(),
                description = etDescription.text?.toString().orEmpty(),
                imageUrl = etImageUrl.text?.toString().orEmpty()
            )
        }

        findViewById<View>(R.id.btnCancelForm).setOnClickListener { finish() }
        findViewById<View>(R.id.btnCancelBottom).setOnClickListener { finish() }
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.tvTitle)
        etName = findViewById(R.id.etName)
        etSpecies = findViewById(R.id.etSpecies)
        etBreed = findViewById(R.id.etBreed)
        etAge = findViewById(R.id.etAge)
        ddListingType = findViewById(R.id.ddListingType)
        etPrice = findViewById(R.id.etPrice)
        etDescription = findViewById(R.id.etDescription)
        etImageUrl = findViewById(R.id.etImageUrl)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListingTypeDropdown() {
        val types = listOf("SALE", "TRADE", "BOTH")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, types)
        ddListingType.setAdapter(adapter)
        ddListingType.setText("SALE", false)
    }

    override fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSubmit.isEnabled = !isLoading
    }

    override fun showPet(pet: PetResponse) {
        etName.setText(pet.name.orEmpty())
        etSpecies.setText(pet.species.orEmpty())
        etBreed.setText(pet.breed.orEmpty())
        etAge.setText(pet.age?.toString().orEmpty())
        ddListingType.setText(pet.listingType.orEmpty().ifBlank { "SALE" }, false)
        etPrice.setText(pet.price?.toString().orEmpty())
        etDescription.setText(pet.description.orEmpty())
        etImageUrl.setText(pet.imageUrl.orEmpty())
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun closeScreen() {
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

package com.dev.petmarket_android.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.ui.ImageLoader

class PetAdapter(
    private val onPetClicked: (Long) -> Unit
) : DiffUtilAdapter<PetResponse, PetAdapter.PetViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(items[position], onPetClicked)
    }

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvPetName)
        private val tvMeta = itemView.findViewById<TextView>(R.id.tvPetMeta)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPetPrice)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvPetStatus)
        private val tvDescription = itemView.findViewById<TextView>(R.id.tvPetDescription)
        private val tvOwner = itemView.findViewById<TextView>(R.id.tvPetOwner)
        private val ivPetImage = itemView.findViewById<ImageView>(R.id.ivPetImage)

        fun bind(item: PetResponse, onPetClicked: (Long) -> Unit) {
            tvName.text = item.name.orEmpty().ifBlank { "Unnamed Pet" }
            tvMeta.text = listOfNotNull(item.breed, item.species, item.age?.let { "$it yrs" })
                .filter { it.isNotBlank() }
                .joinToString("  ")
            tvPrice.text = item.price?.let { "$${"%.0f".format(it)}" } ?: "-"
            tvStatus.text = item.listingType.orEmpty()
                .ifBlank { "Sale" }
                .lowercase()
                .replaceFirstChar { it.uppercase() }
            tvDescription.text = item.description.orEmpty()
                .ifBlank { item.status.orEmpty().ifBlank { "Available marketplace listing" } }
            tvOwner.text = item.ownerName
                ?.takeIf { it.isNotBlank() }
                ?.let { "Listed by $it" }
                .orEmpty()
            ImageLoader.load(ivPetImage, item.imageUrl)

            itemView.setOnClickListener {
                onPetClicked(item.id)
            }
        }
    }
}

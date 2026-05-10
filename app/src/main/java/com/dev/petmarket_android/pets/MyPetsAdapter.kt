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

class MyPetsAdapter(
    private val onEditClicked: (Long) -> Unit,
    private val onDeleteClicked: (Long) -> Unit
) : DiffUtilAdapter<PetResponse, MyPetsAdapter.MyPetViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_pet_card, parent, false)
        return MyPetViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPetViewHolder, position: Int) {
        holder.bind(items[position], onEditClicked, onDeleteClicked)
    }

    class MyPetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvPetName)
        private val tvMeta = itemView.findViewById<TextView>(R.id.tvPetMeta)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPetPrice)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvPetStatus)
        private val ivPetImage = itemView.findViewById<ImageView>(R.id.ivPetImage)
        private val btnEdit = itemView.findViewById<TextView>(R.id.btnEdit)
        private val btnDelete = itemView.findViewById<TextView>(R.id.btnDelete)

        fun bind(
            item: PetResponse,
            onEditClicked: (Long) -> Unit,
            onDeleteClicked: (Long) -> Unit
        ) {
            tvName.text = item.name.orEmpty().ifBlank { "Unnamed Pet" }
            tvMeta.text = listOfNotNull(item.breed, item.species, item.age?.let { "$it yrs" })
                .filter { it.isNotBlank() }
                .joinToString("  ")
            tvPrice.text = item.price?.let { "$${"%.0f".format(it)}" } ?: ""
            tvStatus.text = item.listingType.orEmpty().ifBlank { "Sale" }.lowercase().replaceFirstChar { it.uppercase() }
            ImageLoader.load(ivPetImage, item.imageUrl)

            val canDelete = item.status.orEmpty().equals("AVAILABLE", ignoreCase = true)
            btnDelete.isEnabled = canDelete
            btnDelete.alpha = if (canDelete) 1f else 0.45f
            btnDelete.text = if (canDelete) {
                itemView.context.getString(R.string.delete)
            } else {
                "Purchased"
            }

            btnEdit.setOnClickListener { onEditClicked(item.id) }
            btnDelete.setOnClickListener { onDeleteClicked(item.id) }
        }
    }
}

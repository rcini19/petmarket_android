package com.dev.petmarket_android.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.PetResponse

class AdminPetAdapter(
    private val onDelete: (Long) -> Unit
) : DiffUtilAdapter<PetResponse, AdminPetAdapter.AdminPetViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminPetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_pet_card, parent, false)
        return AdminPetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminPetViewHolder, position: Int) {
        holder.bind(items[position], onDelete)
    }

    class AdminPetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvBreed = itemView.findViewById<TextView>(R.id.tvBreed)
        private val tvOwner = itemView.findViewById<TextView>(R.id.tvOwner)
        private val tvType = itemView.findViewById<TextView>(R.id.tvType)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)

        fun bind(item: PetResponse, onDelete: (Long) -> Unit) {
            tvName.text = item.name.orEmpty().ifBlank { "Unnamed Pet" }
            tvBreed.text = item.breed.orEmpty().ifBlank { item.species.orEmpty().ifBlank { "-" } }
            tvOwner.text = item.ownerName.orEmpty().ifBlank { "Unknown owner" }
            tvType.text = item.listingType.orEmpty().ifBlank { "UNKNOWN" }
            tvPrice.text = item.price?.let { "$${"%.0f".format(it)}" } ?: "-"
            tvStatus.text = item.status.orEmpty()
                .ifBlank { "available" }
                .lowercase()
            btnDelete.setOnClickListener { onDelete(item.id) }
        }
    }
}

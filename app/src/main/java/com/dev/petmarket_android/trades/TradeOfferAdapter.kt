package com.dev.petmarket_android.trades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.TradeOfferResponse

class TradeOfferAdapter(
    private val onAccept: (Long) -> Unit,
    private val onReject: (Long) -> Unit
) : DiffUtilAdapter<TradeOfferResponse, TradeOfferAdapter.TradeOfferViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeOfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trade_offer_card, parent, false)
        return TradeOfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeOfferViewHolder, position: Int) {
        holder.bind(items[position], onAccept, onReject)
    }

    class TradeOfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOfferedPet = itemView.findViewById<TextView>(R.id.tvOfferedPet)
        private val tvRequestedPet = itemView.findViewById<TextView>(R.id.tvRequestedPet)
        private val tvOfferedBy = itemView.findViewById<TextView>(R.id.tvOfferedBy)
        private val tvTradeDate = itemView.findViewById<TextView>(R.id.tvTradeDate)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val btnAccept = itemView.findViewById<Button>(R.id.btnAccept)
        private val btnReject = itemView.findViewById<Button>(R.id.btnReject)

        fun bind(item: TradeOfferResponse, onAccept: (Long) -> Unit, onReject: (Long) -> Unit) {
            tvOfferedPet.text = item.offeredPetName.orEmpty().ifBlank { "Unknown offered pet" }
            tvRequestedPet.text = item.requestedPetName.orEmpty().ifBlank { "Unknown requested pet" }
            tvOfferedBy.text = "From ${item.offeringUserName.orEmpty().ifBlank { "Unknown user" }}"
            tvTradeDate.text = item.date ?: item.createdAt ?: ""
            tvStatus.text = item.status.orEmpty().ifBlank { "Pending" }.lowercase().replaceFirstChar { it.uppercase() }

            val isPending = item.status.orEmpty().equals("PENDING", ignoreCase = true)
            btnAccept.isEnabled = isPending
            btnReject.isEnabled = isPending

            btnAccept.setOnClickListener { onAccept(item.id) }
            btnReject.setOnClickListener { onReject(item.id) }
        }
    }
}

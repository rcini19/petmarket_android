package com.dev.petmarket_android.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.TradeOfferResponse

class TradeHistoryAdapter : DiffUtilAdapter<TradeOfferResponse, TradeHistoryAdapter.TradeHistoryViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trade_history_card, parent, false)
        return TradeHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeHistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class TradeHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvSubtitle = itemView.findViewById<TextView>(R.id.tvSubtitle)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

        fun bind(item: TradeOfferResponse) {
            tvTitle.text = item.title ?: "${item.offeredPetName ?: "Unknown"} -> ${item.requestedPetName ?: "Unknown"}"
            tvSubtitle.text = item.subtitle ?: item.offeringUserName ?: "Trade offer"
            tvDate.text = item.date ?: item.createdAt ?: "-"
            tvStatus.text = item.status ?: "UNKNOWN"
        }
    }
}

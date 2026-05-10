package com.dev.petmarket_android.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.OrderHistoryResponse

class OrderHistoryAdapter : DiffUtilAdapter<OrderHistoryResponse, OrderHistoryAdapter.OrderViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history_card, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvSubtitle = itemView.findViewById<TextView>(R.id.tvSubtitle)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvAmount = itemView.findViewById<TextView>(R.id.tvAmount)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

        fun bind(item: OrderHistoryResponse) {
            val title = item.title ?: item.petName ?: "Order #${item.id}"
            val subtitle = item.subtitle ?: "Pet transaction"
            val date = item.date ?: item.createdAt ?: "-"
            val amount = item.amount ?: item.totalPrice ?: 0.0
            val status = item.status ?: "UNKNOWN"

            tvTitle.text = title
            tvSubtitle.text = subtitle
            tvDate.text = date
            tvAmount.text = "$${"%.2f".format(amount)}"
            tvStatus.text = status
        }
    }
}

package com.dev.petmarket_android.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.AdminUserResponse

class AdminUserAdapter(
    private val onSuspend: (Long) -> Unit
) : DiffUtilAdapter<AdminUserResponse, AdminUserAdapter.AdminUserViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user_card, parent, false)
        return AdminUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) {
        holder.bind(items[position], onSuspend)
    }

    class AdminUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvEmail = itemView.findViewById<TextView>(R.id.tvEmail)
        private val tvRole = itemView.findViewById<TextView>(R.id.tvRole)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val tvActivity = itemView.findViewById<TextView>(R.id.tvActivity)
        private val btnSuspend = itemView.findViewById<Button>(R.id.btnSuspend)

        fun bind(item: AdminUserResponse, onSuspend: (Long) -> Unit) {
            tvName.text = item.fullName.orEmpty().ifBlank { "Unnamed User" }
            tvEmail.text = item.email.orEmpty().ifBlank { "-" }
            tvRole.text = item.role.orEmpty().ifBlank { "USER" }

            val suspended = item.suspended == true
            tvStatus.text = if (suspended) "SUSPENDED" else "ACTIVE"

            val orders = item.orders ?: item.purchases ?: 0
            val trades = item.tradeOffers ?: item.trades ?: 0
            tvActivity.text = "$orders orders • $trades trade offers"

            val isAdmin = item.role.orEmpty().equals("ADMIN", ignoreCase = true)
            btnSuspend.isEnabled = !suspended && !isAdmin
            btnSuspend.setOnClickListener { onSuspend(item.id) }
        }
    }
}

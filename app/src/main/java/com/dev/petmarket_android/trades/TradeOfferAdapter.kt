package com.dev.petmarket_android.trades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.adapter.DiffUtilAdapter
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.util.TradeOfferRules
import com.dev.petmarket_android.common.util.TradeOfferRules.Direction

class TradeOfferAdapter(
    private val sessionManager: SessionManager,
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
        holder.bind(items[position], sessionManager, onAccept, onReject)
    }

    class TradeOfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTradeType = itemView.findViewById<TextView>(R.id.tvTradeType)
        private val tvOfferedPet = itemView.findViewById<TextView>(R.id.tvOfferedPet)
        private val tvRequestedPet = itemView.findViewById<TextView>(R.id.tvRequestedPet)
        private val tvOfferedPetLabel = itemView.findViewById<TextView>(R.id.tvOfferedPetLabel)
        private val tvRequestedPetLabel = itemView.findViewById<TextView>(R.id.tvRequestedPetLabel)
        private val tvOfferedBy = itemView.findViewById<TextView>(R.id.tvOfferedBy)
        private val tvTradeDate = itemView.findViewById<TextView>(R.id.tvTradeDate)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val rowTradeActions = itemView.findViewById<LinearLayout>(R.id.rowTradeActions)
        private val tvActionState = itemView.findViewById<TextView>(R.id.tvActionState)
        private val btnAccept = itemView.findViewById<Button>(R.id.btnAccept)
        private val btnReject = itemView.findViewById<Button>(R.id.btnReject)

        fun bind(
            item: TradeOfferResponse,
            sessionManager: SessionManager,
            onAccept: (Long) -> Unit,
            onReject: (Long) -> Unit
        ) {
            val context = itemView.context
            val direction = TradeOfferRules.direction(item, sessionManager)
            val canRespond = TradeOfferRules.needsResponse(item, sessionManager)

            tvTradeType.text = if (direction == Direction.INCOMING) {
                context.getString(R.string.trade_type_incoming)
            } else {
                context.getString(R.string.trade_type_outgoing)
            }
            tvOfferedPet.text = item.resolvedOfferedPetName.orEmpty().ifBlank { "Unknown offered pet" }
            tvRequestedPet.text = item.resolvedRequestedPetName.orEmpty().ifBlank { "Unknown requested pet" }
            tvOfferedPetLabel.text = if (direction == Direction.INCOMING) {
                context.getString(R.string.trade_they_offer)
            } else {
                context.getString(R.string.trade_you_offer)
            }
            tvRequestedPetLabel.text = if (direction == Direction.INCOMING) {
                context.getString(R.string.trade_for_your)
            } else {
                context.getString(R.string.trade_for_their)
            }
            tvOfferedBy.text = if (direction == Direction.OUTGOING) {
                context.getString(R.string.trade_offer_by_name, currentUserDisplayName(sessionManager))
            } else {
                context.getString(
                    R.string.trade_offer_by_name,
                    item.resolvedOfferingUserName.orEmpty().ifBlank { "Unknown user" }
                )
            }
            tvTradeDate.text = item.date ?: item.createdAt ?: ""
            tvStatus.text = item.status.orEmpty().ifBlank { "Pending" }.lowercase().replaceFirstChar { it.uppercase() }
            styleStatus(item)

            rowTradeActions.visibility = if (canRespond) View.VISIBLE else View.GONE
            tvActionState.visibility = if (canRespond) View.GONE else View.VISIBLE
            tvActionState.text = actionStateText(item, direction)
            btnAccept.isEnabled = canRespond
            btnReject.isEnabled = canRespond

            btnAccept.setOnClickListener { onAccept(item.id) }
            btnReject.setOnClickListener { onReject(item.id) }
        }

        private fun styleStatus(item: TradeOfferResponse) {
            val context = itemView.context
            when {
                TradeOfferRules.isAccepted(item) -> {
                    tvStatus.setBackgroundResource(R.drawable.bg_green_badge)
                    tvStatus.setTextColor(context.getColor(R.color.success_600))
                }
                TradeOfferRules.isRejected(item) -> {
                    tvStatus.setBackgroundResource(R.drawable.bg_field_soft)
                    tvStatus.setTextColor(context.getColor(R.color.pm_error))
                }
                else -> {
                    tvStatus.setBackgroundResource(R.drawable.bg_yellow_badge)
                    tvStatus.setTextColor(context.getColor(R.color.warning_600))
                }
            }
        }

        private fun actionStateText(item: TradeOfferResponse, direction: Direction): String {
            val context = itemView.context
            return when {
                TradeOfferRules.isAccepted(item) -> context.getString(R.string.accepted)
                TradeOfferRules.isRejected(item) -> context.getString(R.string.rejected)
                direction == Direction.OUTGOING -> context.getString(R.string.trade_waiting_for_owner)
                else -> context.getString(R.string.trade_waiting_for_response)
            }
        }

        private fun currentUserDisplayName(sessionManager: SessionManager): String {
            return sessionManager.getFullName().orEmpty()
                .ifBlank { sessionManager.getEmail().orEmpty().substringBefore("@") }
                .ifBlank { "you" }
        }
    }
}

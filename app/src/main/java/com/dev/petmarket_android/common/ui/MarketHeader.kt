package com.dev.petmarket_android.common.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dev.petmarket_android.MainActivity
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.dashboard.DashboardActivity
import com.dev.petmarket_android.pets.BrowsePetsActivity
import com.dev.petmarket_android.pets.MyPetsActivity
import com.dev.petmarket_android.profile.ProfileActivity
import com.dev.petmarket_android.trades.TradesActivity

object MarketHeader {

    fun setup(activity: Activity, currentNavItemId: Int? = null) {
        val session = SessionManager(activity.applicationContext)
        val profileClick = View.OnClickListener { navigate(activity, ProfileActivity::class.java) }
        val initial = session.getFullName()
            ?.trim()
            ?.firstOrNull()
            ?.uppercaseChar()
            ?.toString()
            ?: session.getEmail()
                ?.trim()
                ?.firstOrNull()
                ?.uppercaseChar()
                ?.toString()
            ?: "U"
        val profileImageUrl = session.getProfileImageUrl().orEmpty()

        activity.findViewById<View?>(R.id.headerAvatarContainer)?.setOnClickListener(profileClick)
        activity.findViewById<TextView?>(R.id.tvHeaderAvatar)?.apply {
            text = initial
            visibility = if (profileImageUrl.isBlank()) View.VISIBLE else View.GONE
            setOnClickListener(profileClick)
        }
        activity.findViewById<ImageView?>(R.id.ivHeaderAvatar)?.apply {
            visibility = if (profileImageUrl.isBlank()) View.GONE else View.VISIBLE
            if (profileImageUrl.isNotBlank()) {
                ImageLoader.load(this, profileImageUrl, circleCrop = true)
            }
            setOnClickListener(profileClick)
        }

        activity.findViewById<View?>(R.id.btnHeaderLogout)?.setOnClickListener {
            session.clearSession()
            val intent = Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            activity.startActivity(intent)
            activity.finish()
        }

        setupPill(activity, R.id.navDashboardPill, R.id.nav_dashboard, DashboardActivity::class.java, currentNavItemId)
        setupPill(activity, R.id.navBrowsePill, R.id.nav_browse, BrowsePetsActivity::class.java, currentNavItemId)
        setupPill(activity, R.id.navMyPetsPill, R.id.nav_my_pets, MyPetsActivity::class.java, currentNavItemId)
        setupPill(activity, R.id.navTradesPill, R.id.nav_trades, TradesActivity::class.java, currentNavItemId)
    }

    private fun setupPill(
        activity: Activity,
        viewId: Int,
        navItemId: Int,
        destination: Class<*>,
        currentNavItemId: Int?
    ) {
        activity.findViewById<View?>(viewId)?.apply {
            isSelected = currentNavItemId == navItemId
            setOnClickListener {
                if (activity::class.java != destination) {
                    navigate(activity, destination)
                }
            }
        }
    }

    private fun navigate(activity: Activity, destination: Class<*>) {
        val intent = Intent(activity, destination).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        activity.startActivity(intent)
    }
}

package com.dev.petmarket_android.common.ui

import androidx.viewbinding.ViewBinding

/**
 * Base activity providing the shared market header navigation.
 * Extends BaseViewBindingActivity to provide ViewBinding + CoroutineScope.
 */
abstract class BaseBottomNavActivity<VB : ViewBinding> : BaseViewBindingActivity<VB>() {

    protected fun setupBottomNavigation() {
        MarketHeader.setup(this, getCurrentNavItemId())
    }

    /**
     * Override to return the nav item ID for this activity.
     * This ensures the correct item is highlighted when the activity is displayed.
     */
    abstract fun getCurrentNavItemId(): Int
}

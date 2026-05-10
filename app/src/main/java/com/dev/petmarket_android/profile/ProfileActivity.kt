package com.dev.petmarket_android.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.petmarket_android.R
import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.storage.SessionManager
import com.dev.petmarket_android.common.ui.BaseBottomNavActivity
import com.dev.petmarket_android.common.ui.ImageLoader
import com.dev.petmarket_android.data.ProfileRepository
import com.dev.petmarket_android.databinding.ActivityProfileBinding

class ProfileActivity : BaseBottomNavActivity<ActivityProfileBinding>(), ProfileContract.View {

    override fun createBinding() = ActivityProfileBinding.inflate(layoutInflater)

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var orderAdapter: OrderHistoryAdapter
    private lateinit var tradeAdapter: TradeHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNavigation()

        val repository = ProfileRepository(applicationContext)
        val model = ProfileModel(repository)
        presenter = ProfilePresenter(this, model)

        orderAdapter = OrderHistoryAdapter()
        tradeAdapter = TradeHistoryAdapter()

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = orderAdapter
        binding.rvOrders.isNestedScrollingEnabled = false

        binding.rvTrades.layoutManager = LinearLayoutManager(this)
        binding.rvTrades.adapter = tradeAdapter
        binding.rvTrades.isNestedScrollingEnabled = false

        val session = SessionManager(applicationContext)
        binding.tvFullName.text = session.getFullName().orEmpty().ifBlank { getString(R.string.profile_unknown_name) }
        binding.tvEmail.text = getString(R.string.profile_unknown_email)
        binding.tvRole.text = session.getRole()
        binding.tvAccountTypeValue.text = session.getRole()
        binding.tvMemberSinceValue.text = "-"
        ImageLoader.load(binding.ivProfileImage, null, circleCrop = true)
        setProfileEditing(false)

        binding.btnEditProfile.setOnClickListener {
            setProfileEditing(true)
        }

        binding.btnCancelEdit.setOnClickListener {
            setProfileEditing(false)
        }

        binding.btnSave.setOnClickListener {
            presenter.onSaveProfile(
                fullName = binding.etFullName.text?.toString().orEmpty(),
                email = binding.etEmail.text?.toString().orEmpty()
            )
        }

        binding.btnSaveImage.setOnClickListener {
            presenter.onSaveProfileImage(binding.etProfileImageUrl.text?.toString().orEmpty())
        }

        binding.btnChangePassword.setOnClickListener {
            presenter.onChangePassword(
                currentPassword = binding.etCurrentPassword.text?.toString().orEmpty(),
                newPassword = binding.etNewPassword.text?.toString().orEmpty(),
                confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()
            )
        }

        presenter.loadData()
    }

    override fun getCurrentNavItemId(): Int = R.id.nav_profile

    override fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnEditProfile.isEnabled = !isLoading
        binding.btnSave.isEnabled = !isLoading
        binding.btnSaveImage.isEnabled = !isLoading
        binding.btnCancelEdit.isEnabled = !isLoading
        binding.btnChangePassword.isEnabled = !isLoading
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        setProfileEditing(false)
        binding.etCurrentPassword.text?.clear()
        binding.etNewPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
    }

    override fun showProfile(profile: ProfileResponse) {
        val name = profile.fullName.orEmpty().ifBlank { getString(R.string.profile_unknown_name) }
        val email = profile.email.orEmpty().ifBlank { getString(R.string.profile_unknown_email) }
        val role = profile.role.orEmpty().ifBlank { getString(R.string.profile_default_role) }
        val profileImageUrl = profile.resolvedProfileImageUrl.orEmpty()

        SessionManager(applicationContext).updateProfile(
            email = profile.email,
            fullName = profile.fullName,
            role = profile.role,
            profileImageUrl = profileImageUrl
        )

        binding.tvFullName.text = name
        binding.tvEmail.text = email
        binding.tvRole.text = role
        binding.tvAccountTypeValue.text = profile.accountType.orEmpty().ifBlank { role }
        binding.tvMemberSinceValue.text = profile.memberSince.orEmpty().ifBlank { "-" }
        binding.tvAvatarInitial.text = name.firstOrNull()?.uppercaseChar()?.toString()
            ?: email.firstOrNull()?.uppercaseChar()?.toString()
            ?: "U"
        binding.tvAvatarInitial.visibility = if (profileImageUrl.isBlank()) View.VISIBLE else View.GONE

        binding.etFullName.setText(profile.fullName.orEmpty())
        binding.etEmail.setText(profile.email.orEmpty())
        binding.etProfileImageUrl.setText(profileImageUrl)
        ImageLoader.load(binding.ivProfileImage, profileImageUrl, circleCrop = true)
    }

    override fun showOrderHistory(items: List<OrderHistoryResponse>) {
        orderAdapter.submitList(items)
        binding.tvOrdersCount.text = items.size.toString()
        val spent = items.sumOf { it.amount ?: it.totalPrice ?: 0.0 }
        binding.tvSpentValue.text = "$${"%.0f".format(spent)}"
        binding.tvOrdersEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showTradeHistory(items: List<TradeOfferResponse>) {
        tradeAdapter.submitList(items)
        binding.tvTradesCount.text = items.size.toString()
        binding.tvTradesEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun setProfileEditing(isEditing: Boolean) {
        binding.profileEditPanel.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.btnEditProfile.visibility = if (isEditing) View.GONE else View.VISIBLE
    }
}

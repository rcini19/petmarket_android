package com.dev.petmarket_android.profile

class ProfilePresenter(
    private var view: ProfileContract.View?,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    override fun loadData() {
        view?.showLoading(true)
        model.loadData(
            onSuccess = { profile, orders, trades ->
                view?.showLoading(false)
                view?.showProfile(profile)
                view?.showOrderHistory(orders)
                view?.showTradeHistory(trades)
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onSaveProfile(fullName: String, email: String) {
        if (fullName.trim().isEmpty() || email.trim().isEmpty()) {
            view?.showError("Full name and email are required")
            return
        }

        view?.showLoading(true)
        model.updateProfile(
            fullName = fullName.trim(),
            email = email.trim(),
            onSuccess = { profile ->
                view?.showLoading(false)
                view?.showProfile(profile)
                view?.showSuccess("Profile updated")
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onSaveProfileImage(profileImageUrl: String) {
        if (profileImageUrl.trim().isEmpty()) {
            view?.showError("Profile image URL is required")
            return
        }

        view?.showLoading(true)
        model.updateProfileImage(
            profileImageUrl = profileImageUrl.trim(),
            onSuccess = { profile ->
                view?.showLoading(false)
                view?.showProfile(profile)
                view?.showSuccess("Profile photo updated")
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onChangePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            view?.showError("All password fields are required")
            return
        }

        if (newPassword != confirmPassword) {
            view?.showError("New password and confirmation do not match")
            return
        }

        view?.showLoading(true)
        model.changePassword(
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword,
            onSuccess = {
                view?.showLoading(false)
                view?.showSuccess("Password changed successfully")
            },
            onFailure = {
                view?.showLoading(false)
                view?.showError(it)
            }
        )
    }

    override fun onDestroy() {
        view = null
    }
}

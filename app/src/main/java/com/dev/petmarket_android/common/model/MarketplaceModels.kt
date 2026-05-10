package com.dev.petmarket_android.common.model

import com.google.gson.annotations.SerializedName

data class PetResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("species") val species: String?,
    @SerializedName("breed") val breed: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("listingType") val listingType: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("ownerName") val ownerName: String?,
    @SerializedName("status") val status: String?
)

data class TradeOfferResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("offeredPetId") val offeredPetId: Long? = null,
    @SerializedName("requestedPetId") val requestedPetId: Long? = null,
    @SerializedName("offeredPetName") val offeredPetName: String? = null,
    @SerializedName("requestedPetName") val requestedPetName: String? = null,
    @SerializedName("offeringUserName") val offeringUserName: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("amount") val amount: Double? = null
)

data class TradeOfferRequest(
    @SerializedName("offeredPetId") val offeredPetId: Long,
    @SerializedName("requestedPetId") val requestedPetId: Long
)

data class OrderRequest(
    @SerializedName("petId") val petId: Long,
    @SerializedName("totalPrice") val totalPrice: Double
)

data class OrderResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("petName") val petName: String?,
    @SerializedName("totalPrice") val totalPrice: Double?
)

data class PetRequest(
    @SerializedName("name") val name: String,
    @SerializedName("species") val species: String,
    @SerializedName("breed") val breed: String,
    @SerializedName("age") val age: Int,
    @SerializedName("listingType") val listingType: String,
    @SerializedName("price") val price: Double?,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String
)

data class ProfileResponse(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("accountType") val accountType: String? = null,
    @SerializedName("memberSince") val memberSince: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("profilePictureUrl") val profilePictureUrl: String? = null,
    @SerializedName("profilePicture") val profilePicture: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
) {
    val resolvedProfileImageUrl: String?
        get() = firstNonBlank(profileImageUrl, profilePictureUrl, profilePicture, avatarUrl, imageUrl)
}

data class PhotoUploadResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("fileReference") val fileReference: String? = null,
    @SerializedName("profile") val profile: ProfileResponse? = null
)

data class ProfileUpdateRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String
)

data class ProfileImageUpdateRequest(
    @SerializedName("profileImageUrl") val profileImageUrl: String
)

data class PasswordUpdateRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class OrderHistoryResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("petName") val petName: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalPrice") val totalPrice: Double? = null,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("date") val date: String? = null
)

data class AdminUserResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("suspended") val suspended: Boolean? = null,
    @SerializedName("joinedAt") val joinedAt: String? = null,
    @SerializedName("orders") val orders: Int? = null,
    @SerializedName("purchases") val purchases: Int? = null,
    @SerializedName("tradeOffers") val tradeOffers: Int? = null,
    @SerializedName("trades") val trades: Int? = null
)

private fun firstNonBlank(vararg values: String?): String? {
    return values.firstOrNull { !it.isNullOrBlank() }?.trim()
}

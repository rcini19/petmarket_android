package com.dev.petmarket_android.common.model

import com.google.gson.annotations.SerializedName

data class PetResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String? = null,
    @SerializedName("species") val species: String? = null,
    @SerializedName("breed") val breed: String? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("listingType") val listingType: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName(value = "ownerId", alternate = ["owner_id", "userId", "user_id", "sellerId", "seller_id"]) val ownerId: Long? = null,
    @SerializedName(value = "owner", alternate = ["user", "seller"]) val owner: PetOwnerResponse? = null,
    @SerializedName(value = "ownerName", alternate = ["ownerFullName", "sellerName", "userName"]) val ownerName: String? = null,
    @SerializedName(value = "ownerEmail", alternate = ["owner_email", "sellerEmail", "userEmail"]) val ownerEmail: String? = null,
    @SerializedName(value = "ownerUsername", alternate = ["ownerUserName", "username", "sellerUsername"]) val ownerUsername: String? = null,
    @SerializedName(value = "ownedByCurrentUser", alternate = ["isOwner", "mine", "ownedByMe"]) val ownedByCurrentUser: Boolean? = null,
    @SerializedName("status") val status: String? = null
)

data class PetOwnerResponse(
    @SerializedName(value = "id", alternate = ["userId", "user_id"]) val id: Long? = null,
    @SerializedName(value = "fullName", alternate = ["name", "displayName"]) val fullName: String? = null,
    @SerializedName(value = "username", alternate = ["userName"]) val username: String? = null,
    @SerializedName("email") val email: String? = null
)

data class TradeOfferResponse(
    @SerializedName("id") val id: Long,
    @SerializedName(value = "offeredPetId", alternate = ["offered_pet_id", "offerPetId", "sourcePetId"]) val offeredPetId: Long? = null,
    @SerializedName(value = "requestedPetId", alternate = ["requested_pet_id", "requestPetId", "targetPetId"]) val requestedPetId: Long? = null,
    @SerializedName(value = "offeredPet", alternate = ["offered", "offerPet", "sourcePet"]) val offeredPet: PetResponse? = null,
    @SerializedName(value = "requestedPet", alternate = ["requested", "requestPet", "targetPet"]) val requestedPet: PetResponse? = null,
    @SerializedName(value = "offeredPetName", alternate = ["offered_pet_name", "offerPetName", "sourcePetName"]) val offeredPetName: String? = null,
    @SerializedName(value = "requestedPetName", alternate = ["requested_pet_name", "requestPetName", "targetPetName"]) val requestedPetName: String? = null,
    @SerializedName(value = "offeringUserId", alternate = ["offering_user_id", "offerById", "offeredById"]) val offeringUserId: Long? = null,
    @SerializedName(value = "offeringUser", alternate = ["offerByUser", "offeredByUser"]) val offeringUser: PetOwnerResponse? = null,
    @SerializedName(value = "offeringUserName", alternate = ["offerBy", "offeredBy", "offerByName", "offeredByName"]) val offeringUserName: String? = null,
    @SerializedName(value = "offeredPetOwnerId", alternate = ["offeredOwnerId", "offered_pet_owner_id"]) val offeredPetOwnerId: Long? = null,
    @SerializedName(value = "offeredPetOwnerName", alternate = ["offeredOwnerName"]) val offeredPetOwnerName: String? = null,
    @SerializedName(value = "requestedPetOwnerId", alternate = ["requestedOwnerId", "requested_pet_owner_id", "targetOwnerId"]) val requestedPetOwnerId: Long? = null,
    @SerializedName(value = "requestedPetOwnerName", alternate = ["requestedOwnerName", "targetOwnerName"]) val requestedPetOwnerName: String? = null,
    @SerializedName(value = "direction", alternate = ["type"]) val direction: String? = null,
    @SerializedName(value = "needsResponse", alternate = ["requiresResponse"]) val needsResponse: Boolean? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("respondedAt") val respondedAt: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("amount") val amount: Double? = null
) {
    val resolvedOfferedPetId: Long?
        get() = offeredPetId ?: offeredPet?.id

    val resolvedRequestedPetId: Long?
        get() = requestedPetId ?: requestedPet?.id

    val resolvedOfferedPetName: String?
        get() = firstNonBlank(offeredPetName, offeredPet?.name)

    val resolvedRequestedPetName: String?
        get() = firstNonBlank(requestedPetName, requestedPet?.name)

    val resolvedOfferingUserName: String?
        get() = firstNonBlank(offeringUserName, offeringUser?.fullName, offeringUser?.username, offeringUser?.email)
}

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
    @SerializedName(value = "id", alternate = ["userId", "user_id"]) val id: Long? = null,
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

private fun firstNonBlank(vararg values: String?): String? {
    return values.firstOrNull { !it.isNullOrBlank() }?.trim()
}

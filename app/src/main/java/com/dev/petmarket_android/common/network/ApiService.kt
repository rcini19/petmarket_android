package com.dev.petmarket_android.common.network

import com.dev.petmarket_android.common.model.AuthResponse
import com.dev.petmarket_android.common.model.LoginRequest
import com.dev.petmarket_android.common.model.OrderHistoryResponse
import com.dev.petmarket_android.common.model.OrderRequest
import com.dev.petmarket_android.common.model.OrderResponse
import com.dev.petmarket_android.common.model.PasswordUpdateRequest
import com.dev.petmarket_android.common.model.ProfileResponse
import com.dev.petmarket_android.common.model.PhotoUploadResponse
import com.dev.petmarket_android.common.model.ProfileImageUpdateRequest
import com.dev.petmarket_android.common.model.ProfileUpdateRequest
import com.dev.petmarket_android.common.model.PetRequest
import com.dev.petmarket_android.common.model.PetResponse
import com.dev.petmarket_android.common.model.RegisterRequest
import com.dev.petmarket_android.common.model.TradeOfferRequest
import com.dev.petmarket_android.common.model.TradeOfferResponse
import com.dev.petmarket_android.common.model.PaginatedResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @POST
    fun login(@Url endpoint: String, @Body body: LoginRequest): Call<AuthResponse>

    @POST
    fun register(@Url endpoint: String, @Body body: RegisterRequest): Call<AuthResponse>

    // Pets endpoints with pagination
    @GET
    fun getPets(
        @Url endpoint: String,
        @QueryMap params: Map<String, String>,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Call<PaginatedResponse<PetResponse>>

    @GET
    fun getMyPets(
        @Url endpoint: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Call<PaginatedResponse<PetResponse>>

    @GET
    fun getPetById(@Url endpoint: String): Call<PetResponse>

    // Trade offers with pagination
    @GET
    fun getTradeOffers(
        @Url endpoint: String,
        @QueryMap params: Map<String, String> = emptyMap(),
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Call<PaginatedResponse<TradeOfferResponse>>

    @POST
    fun createOrder(@Url endpoint: String, @Body body: OrderRequest): Call<OrderResponse>

    @POST
    fun purchasePet(@Url endpoint: String, @Body body: OrderRequest): Call<ResponseBody>

    @POST
    fun purchasePet(@Url endpoint: String): Call<ResponseBody>

    @POST
    fun createPet(@Url endpoint: String, @Body body: PetRequest): Call<PetResponse>

    @PUT
    fun updatePet(@Url endpoint: String, @Body body: PetRequest): Call<PetResponse>

    @DELETE
    fun deletePet(@Url endpoint: String): Call<Unit>

    @POST
    fun createTradeOffer(@Url endpoint: String, @Body body: TradeOfferRequest): Call<TradeOfferResponse>

    @PUT
    fun acceptTradeOffer(@Url endpoint: String): Call<TradeOfferResponse>

    @PUT
    fun rejectTradeOffer(@Url endpoint: String): Call<TradeOfferResponse>

    @GET
    fun getProfile(@Url endpoint: String): Call<ProfileResponse>

    @PUT
    fun updateProfile(@Url endpoint: String, @Body body: ProfileUpdateRequest): Call<ProfileResponse>

    @PUT
    fun updateProfileImage(@Url endpoint: String, @Body body: ProfileImageUpdateRequest): Call<PhotoUploadResponse>

    @PUT
    fun changePassword(@Url endpoint: String, @Body body: PasswordUpdateRequest): Call<ResponseBody>

    // Order history with pagination
    @GET
    fun getOrderHistory(
        @Url endpoint: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Call<List<OrderHistoryResponse>>

    @GET
    fun getOrderHistoryRaw(
        @Url endpoint: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 100
    ): Call<ResponseBody>

    // Trade history with pagination
    @GET
    fun getTradeHistory(
        @Url endpoint: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Call<List<TradeOfferResponse>>

    @GET
    fun getTradeHistoryRaw(
        @Url endpoint: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 100
    ): Call<ResponseBody>

}

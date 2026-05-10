package com.dev.petmarket_android.common.network

import android.content.Context
import android.util.Log
import com.dev.petmarket_android.BuildConfig
import com.dev.petmarket_android.common.storage.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var apiService: ApiService? = null
    private const val REQUEST_TIMEOUT_SECONDS = 30L
    private const val CONNECT_TIMEOUT_SECONDS = 10L
    private const val READ_TIMEOUT_SECONDS = 30L

    fun getService(context: Context): ApiService {
        if (apiService == null) {
            val sessionManager = SessionManager(context.applicationContext)

            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val token = sessionManager.getToken()
                val requestBuilder = original.newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }

            // Only log headers in debug builds to prevent sensitive data from being logged
            val logger = HttpLoggingInterceptor { message ->
                Log.d("HttpLogging", message)
            }.apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.HEADERS
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logger)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/') + "/"

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService!!
    }
}

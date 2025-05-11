package com.example.memematch.ui.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS) // Increase connection timeout
        .readTimeout(90, TimeUnit.SECONDS)    // Increase read timeout
        .writeTimeout(90, TimeUnit.SECONDS)   // Increase write timeout
        .build()

    val api: MemeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://hugely-climbing-moray.ngrok-free.app/")
            .client(client) // Use the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MemeApi::class.java)
    }
}

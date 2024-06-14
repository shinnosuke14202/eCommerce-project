package com.example.ecomapp.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiConfig {

    private const val IP = "192.168.1.75"

    private const val BASE_URL = "http://${IP}:8080/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitWithoutJwt: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private const val SECOND_URL = "http://${IP}:5000/"

    val secondRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(SECOND_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun createOkHttpClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()

        // Thêm interceptor để thêm JWT vào header của mỗi yêu cầu
        httpClient.addInterceptor { chain ->

            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Thêm JWT vào header nếu có
            JwtManager.CURRENT_JWT.let { jwtToken ->
//                Log.d("AA", jwtToken)
                requestBuilder.header("Authorization", "Bearer $jwtToken")
            }

            val request = requestBuilder.build()
            chain.proceed(request)

        }

        return httpClient.build()
    }
}
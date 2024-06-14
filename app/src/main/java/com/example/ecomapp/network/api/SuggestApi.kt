package com.example.ecomapp.network.api

import com.example.ecomapp.data.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SuggestApi {

    @GET("/recommendation")
    suspend fun getSuggestProducts(
        @Query("bookTitles") bookTitle : String
    ): Response<List<Product>>

    @GET("/recommendation/holiday/{date}/{lunar}")
    suspend fun getSuggestProductsForSpecialDay(
        @Path("date") date : String,
        @Path("lunar") lunar : String,
    ): Response<List<Product>>

}
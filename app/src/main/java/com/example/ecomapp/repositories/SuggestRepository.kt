package com.example.ecomapp.repositories

import com.example.ecomapp.data.Product
import com.example.ecomapp.network.ApiConfig
import com.example.ecomapp.network.api.SuggestApi
import retrofit2.Response

class SuggestRepository {

    private val suggestService = ApiConfig.secondRetrofit.create(SuggestApi::class.java)

    suspend fun getSuggestProducts(bookTitles : List<String>) : Response<List<Product>> {
        return suggestService.getSuggestProducts(bookTitles.joinToString("+"))
    }

    suspend fun getSuggestProductsForSpecialDay(date : String, lunar : String) : Response<List<Product>> {
        return suggestService.getSuggestProductsForSpecialDay(date, lunar)
    }

}
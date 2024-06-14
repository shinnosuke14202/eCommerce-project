package com.example.ecomapp.network.api

import com.example.ecomapp.data.Revenue
import com.example.ecomapp.data.SuggestAi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApi {

    @GET("/dashboard/revenue/{startDate}/{endDate}")
    suspend fun getRevenueBetweenDates(
        @Path(value = "startDate") startDate : String,
        @Path(value = "endDate") endDate : String
    ) : Response<List<Revenue>>

    @GET("/dashboard/suggestAi/{number}")
    suspend fun getSuggestByAiData(
        @Path(value = "number") number : Int,
    ) : Response<List<SuggestAi>>

}
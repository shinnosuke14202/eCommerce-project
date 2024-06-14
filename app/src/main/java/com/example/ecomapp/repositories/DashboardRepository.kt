package com.example.ecomapp.repositories

import com.example.ecomapp.data.Revenue
import com.example.ecomapp.data.SuggestAi
import com.example.ecomapp.network.ApiConfig
import com.example.ecomapp.network.api.DashboardApi
import retrofit2.Response

class DashboardRepository {

    private val dashboardService = ApiConfig.retrofit.create(DashboardApi::class.java)

    suspend fun getRevenueBetweenDates(startDate : String, endDate : String) : Response<List<Revenue>> {
        return dashboardService.getRevenueBetweenDates(startDate, endDate)
    }

    suspend fun getSuggestByAiData(number: Int) : Response<List<SuggestAi>> {
        return dashboardService.getSuggestByAiData(number)
    }

}
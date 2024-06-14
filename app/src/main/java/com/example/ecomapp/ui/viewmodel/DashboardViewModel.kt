package com.example.ecomapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomapp.data.Revenue
import com.example.ecomapp.data.SuggestAi
import com.example.ecomapp.repositories.DashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val dashboardRepository = DashboardRepository()

    private val _revenue = MutableLiveData<List<Revenue>>()
    val revenue: MutableLiveData<List<Revenue>>
        get() = _revenue

    private val _suggestAi = MutableLiveData<List<SuggestAi>>()
    val suggestAi: MutableLiveData<List<SuggestAi>>
        get() = _suggestAi

    fun fetchRevenueBetweenDates(startDate: String, endDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = dashboardRepository.getRevenueBetweenDates(startDate, endDate)
            if (response.isSuccessful) {
                revenue.postValue(response.body())
            } else {
                Log.i(
                    "Error",
                    "Error: code = ${response.code()} and message = ${response.message()}"
                )
            }
        }
    }

    fun getSuggestByAiData(number : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = dashboardRepository.getSuggestByAiData(number)
            if (response.isSuccessful) {
                suggestAi.postValue(response.body())
            } else {
                Log.i(
                    "Error",
                    "Error: code = ${response.code()} and message = ${response.message()}"
                )
            }
        }
    }

}
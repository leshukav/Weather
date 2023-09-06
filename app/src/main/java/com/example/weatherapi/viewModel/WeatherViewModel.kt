package com.example.weatherapi.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapi.dto.WeatherData

class WeatherViewModel: ViewModel() {
    val lifeDataCurrent = MutableLiveData<WeatherData>()
    val lifeDataList = MutableLiveData<List<WeatherData>>()
}
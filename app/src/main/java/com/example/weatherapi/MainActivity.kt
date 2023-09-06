package com.example.weatherapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.example.weatherapi.fragment.MainFragment
import com.example.weatherapi.viewModel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      supportFragmentManager
          .beginTransaction()
          .replace(R.id.placeHolder, MainFragment.newInstance())
          .commit()

        setContentView(R.layout.activity_main)
    }
}
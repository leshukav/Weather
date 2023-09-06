package com.example.weatherapi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.R
import com.example.weatherapi.adapter.WeatherAdapter
import com.example.weatherapi.databinding.FragmentHoursBinding
import com.example.weatherapi.dto.WeatherData
import com.example.weatherapi.viewModel.WeatherViewModel
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHoursBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.lifeDataCurrent.observe(viewLifecycleOwner){
            adapter.submitList(getHoursList(it))
            Log.d("MyTag","hours ->  ${it.hours}")
        }
    }

    private fun initRcView(){
        adapter = WeatherAdapter(object : WeatherAdapter.Listener{
            override fun onClick(item: WeatherData) {

            }
        })
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
    }


    private fun getHoursList(item: WeatherData): List<WeatherData>{
        val hoursArray = JSONArray(item.hours)
        val list = arrayListOf<WeatherData>()
        for (i in 0 until hoursArray.length()) {
            val itemWeather = WeatherData(
                item.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
                ""
            )
            list.add(itemWeather)
        }
        Log.d("MyTag", "Lenght list -> ${list.size}")
        return list
    }

    companion object {

        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
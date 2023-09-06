package com.example.weatherapi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.R
import com.example.weatherapi.adapter.WeatherAdapter
import com.example.weatherapi.databinding.FragmentDaysBinding
import com.example.weatherapi.databinding.FragmentHoursBinding
import com.example.weatherapi.dto.WeatherData
import com.example.weatherapi.viewModel.WeatherViewModel


class DaysFragment : Fragment(), WeatherAdapter.Listener {

    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherAdapter
    private val model: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.lifeDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
          //  adapter.submitList(it.subList(1,it.size))
        }
    }

    private fun init() {
        adapter = WeatherAdapter(this@DaysFragment)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance() = DaysFragment()

    }

    override fun onClick(item: WeatherData) {
       model.lifeDataCurrent.value = item
    }
}
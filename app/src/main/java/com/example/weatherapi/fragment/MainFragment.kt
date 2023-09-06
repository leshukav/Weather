package com.example.weatherapi.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapi.DialogManadger
import com.example.weatherapi.adapter.ViewPagerAdapter
import com.example.weatherapi.databinding.FragmentMainBinding
import com.example.weatherapi.dto.WeatherData
import com.example.weatherapi.viewModel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fragmentList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance(),
    )
    private val tabList = listOf(
        "HOURS",
        "DAYS",
    )
    private lateinit var permissionlauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentWeather()
        //requestWeatherData("Sevastopol")
     //   getLocation()

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
        private val API_KEY = "7d830570d85d41209eb163319231004"
        private val BASE_URL ="http://api.weatherapi.com/v1/"
    }

    private fun init() {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = ViewPagerAdapter(activity as FragmentActivity, fragmentList)
        binding.vp.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
        binding.ibSync.setOnClickListener {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            checkLocation()
        }
        binding.ibSearch.setOnClickListener {
            DialogManadger.searchByNameDialog(requireContext(), object : DialogManadger.Listener{
                override fun onClick(name: String?) {
                    if (name != null) {
                        requestWeatherData(name)
                    }
                }

            })
        }
    }

    private fun checkLocation(){
        if(isLocationEnabled()){
            getLocation()
        } else {
            DialogManadger.locationSettingsDialog(requireContext(), object : DialogManadger.Listener {
                override fun onClick(name: String?) {
                   startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return  lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

   private fun getLocation(){
        if (!isLocationEnabled()) {
            Toast.makeText(requireContext(), "Location disabled", Toast.LENGTH_LONG).show()
            return
        }
       val ct = CancellationTokenSource()
       if (ActivityCompat.checkSelfPermission(
               requireContext(),
               Manifest.permission.ACCESS_FINE_LOCATION
           ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
               requireContext(),
               Manifest.permission.ACCESS_COARSE_LOCATION
           ) != PackageManager.PERMISSION_GRANTED
       ) {
           // TODO: Consider calling
           //    ActivityCompat#requestPermissions
           // here to request the missing permissions, and then overriding
           //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
           //                                          int[] grantResults)
           // to handle the case where the user grants the permission. See the documentation
           // for ActivityCompat#requestPermissions for more details.
           return
       }
       fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
           .addOnCompleteListener {
               requestWeatherData("${it.result.latitude},${it.result.longitude}")
           }
   }

    private fun updateCurrentWeather(){
        model.lifeDataCurrent.observe(viewLifecycleOwner){
            val maxMin = "${it.maxTemp}C/${it.minTemp}C"
            with(binding){
                tvCity.text = it.city
                tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMin }
                tvData.text = it.time
                tvCondition.text = it.condition
                tvMaxMin.text = if (it.currentTemp.isEmpty()) "" else maxMin
                Picasso.get().load("https:" + it.imageUrl).into(imWeather)
            }


        }
    }

    private fun permissionListener(){
        permissionlauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            permissionlauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String){
        val url = BASE_URL +
                "forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "Request error --> $error")
                Toast.makeText(context, "City not found", Toast.LENGTH_LONG).show()
                DialogManadger.searchByNameDialog(requireContext(), object : DialogManadger.Listener{
                    override fun onClick(name: String?) {
                        if (name != null) {
                            requestWeatherData(name)
                        }
                    }

                })
            }
        )
        queue.add(request)
    }
    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherData>{
        val list = ArrayList<WeatherData>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name =  mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherData(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.lifeDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherData){
        val item = WeatherData(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.hours
        )

        model.lifeDataCurrent.value = item
    }


}

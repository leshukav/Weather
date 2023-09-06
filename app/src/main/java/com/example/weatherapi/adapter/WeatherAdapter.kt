package com.example.weatherapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapi.R
import com.example.weatherapi.databinding.ListItemBinding
import com.example.weatherapi.dto.WeatherData
import com.squareup.picasso.Picasso

class WeatherAdapter(
    private val listener: Listener
): ListAdapter<WeatherData, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View,val litener: Listener): RecyclerView.ViewHolder(view) {
        val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherData? = null
        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> litener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherData) = with(binding){
            itemTemp = item
            tvDate.text = item.time
            tvCondition.text = item.condition
            tvTemp.text =item.currentTemp.ifEmpty { "${item.maxTemp}C / ${item.minTemp}C" }
            Picasso.get().load("https:" + item.imageUrl).into(iView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
       holder.bind(getItem(position))
    }
    interface Listener{
        fun onClick(item: WeatherData)
    }

    class Comparator: DiffUtil.ItemCallback<WeatherData>(){
        override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
            return oldItem == newItem
        }
    }
}


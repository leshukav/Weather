package com.example.weatherapi.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentAdapter: FragmentActivity, private val list: List<Fragment>
    ): FragmentStateAdapter(fragmentAdapter) {
    override fun getItemCount(): Int {
       return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}
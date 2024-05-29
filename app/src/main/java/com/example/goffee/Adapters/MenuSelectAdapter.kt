package com.example.goffee.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.goffee.Item.IceItem
import com.example.goffee.Item.HotItem

class MenuSelectAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> IceItem()  // Assume you have ColdItem fragment for "Dingin"
            else -> HotItem()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null  // Titles are set in the custom tab view
    }
}

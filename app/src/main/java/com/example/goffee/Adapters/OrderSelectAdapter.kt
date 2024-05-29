package com.example.goffee.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.goffee.Order.HistoryFragment
import com.example.goffee.Order.PendingFragment
import com.example.goffee.Order.UnpaidFragment


class OrderSelectAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UnpaidFragment()
            1 -> PendingFragment()
            else -> HistoryFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null  // Titles are set in the custom tab view
    }
}
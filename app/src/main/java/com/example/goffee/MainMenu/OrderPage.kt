package com.example.goffee.MainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.goffee.Adapters.OrderSelectAdapter
import com.example.goffee.R
import com.google.android.material.tabs.TabLayout


class OrderPage : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_page, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewpager_main)
        tabs = view.findViewById(R.id.tabs_main)


        val fragmentAdapter = OrderSelectAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)

        setupTabIcons()
    }

    private fun setupTabIcons() {
        val tabIcons = arrayOf(
            R.drawable.ic_unpaid,
            R.drawable.ic_pending,
            R.drawable.ic_history
        )
        val tabTitles = arrayOf(
            "Belum Bayar",
            "Dalam Proses",
            "Riwayat"
        )

        for (i in tabTitles.indices) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {
                val customView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
                customView.findViewById<TextView>(R.id.tabText).text = tabTitles[i]
                customView.findViewById<TextView>(R.id.tabText).textSize = 13F
                customView.findViewById<ImageView>(R.id.tabIcon).setImageResource(tabIcons[i])
                tab.customView = customView
            }
        }
    }
}
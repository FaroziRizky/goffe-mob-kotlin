package com.example.goffee.MainMenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.goffee.Adapters.MenuSelectAdapter
import com.example.goffee.CartPage
import com.example.goffee.R
import com.google.android.material.tabs.TabLayout

class MenuPage : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var cartButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewpager_main)
        tabs = view.findViewById(R.id.tabs_main)
        cartButton = view.findViewById(R.id.cartButton)

        cartButton.setOnClickListener {
            activity?.let {
                val intent = Intent(it, CartPage::class.java)
                it.startActivity(intent)
            }
        }

        val fragmentAdapter = MenuSelectAdapter(childFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewPager)

        val tabIndex = arguments?.getInt("TAB_INDEX", 0) ?: 0
        viewPager.currentItem = tabIndex

        setupTabIcons()
    }

    private fun setupTabIcons() {
        val tabIcons = arrayOf(
            R.drawable.cold,  // Replace with your icon for "Dingin"
            R.drawable.ic_hot    // Replace with your icon for "Panas"
        )
        val tabTitles = arrayOf(
            "Dingin",
            "Panas"
        )

        for (i in tabTitles.indices) {
            val tab = tabs.getTabAt(i)
            if (tab != null) {
                val customView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
                customView.findViewById<TextView>(R.id.tabText).text = tabTitles[i]
                customView.findViewById<ImageView>(R.id.tabIcon).setImageResource(tabIcons[i])
                tab.customView = customView
            }
        }
    }
}

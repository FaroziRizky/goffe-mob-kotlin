package com.example.goffee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.goffee.MainMenu.AccountPage
import com.example.goffee.MainMenu.HomePage
import com.example.goffee.MainMenu.MenuPage
import com.example.goffee.MainMenu.OrderPage
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainPage : AppCompatActivity() {
    val fragment = HomePage()
    private lateinit var menu_bottom: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        openMainFragment()
        supportActionBar?.hide()

        menu_bottom = findViewById(R.id.bottom_navigation)
        menu_bottom.setItemSelected(R.id.home)

        menu_bottom.setOnItemSelectedListener {
            when (it) {
                R.id.home -> {
                    openMainFragment()
                }

                R.id.menu -> {
                    val menuPage = MenuPage()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, menuPage).commit()
                }

                R.id.pesanan -> {
                    val instanceFragment = OrderPage()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, instanceFragment).commit()
                }

                R.id.account -> {
                    val accountFragment = AccountPage()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frag_container_nav, accountFragment).commit()
                }
            }
        }
    }

    private fun openMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container_nav, fragment)
        transaction.commit()
    }

    fun setBottomNavigationSelectedItem(itemId: Int) {
        menu_bottom.setItemSelected(itemId)
    }
}

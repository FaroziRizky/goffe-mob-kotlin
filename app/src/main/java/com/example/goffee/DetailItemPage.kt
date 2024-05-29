package com.example.goffee

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.goffee.ApiService.ApiService
import com.example.goffee.Utils.CartService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.Models.MenuModel
import com.example.goffee.api.RetrofitInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailItemPage : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productVariant: TextView
    private lateinit var productInformation: TextView
    private lateinit var productPrice: TextView
    private lateinit var addToCartButton: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_item_page)

        productImage = findViewById(R.id.productImage)
        productName = findViewById(R.id.productName)
        productVariant = findViewById(R.id.productVariant)
        productInformation = findViewById(R.id.productInformation)
        productPrice = findViewById(R.id.productPrice)
        addToCartButton = findViewById(R.id.addToCartButton)
        btnBack = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressed()
            finish()
        }
        val idMenu = intent.getIntExtra("id_menu", -1)
        if (idMenu != -1) {
            fetchProductDetails(idMenu)
        }

        addToCartButton.setOnClickListener {
            CartService.addToCart(this, idMenu)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchProductDetails(idMenu: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getProductDetails(token, idMenu).enqueue(object : Callback<ApiService.productDetail> {
                override fun onResponse(call: Call<ApiService.productDetail>, response: Response<ApiService.productDetail>) {
                    if (response.isSuccessful) {
                        val product = response.body()?.values?.get(0)
                        if (product != null) {
                            updateUI(product)
                        }
                    } else {
                        Toast.makeText(this@DetailItemPage, "Gagal mengambil detail produk", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiService.productDetail>, t: Throwable) {
                    Toast.makeText(this@DetailItemPage, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DetailItemPage", "Error fetching product details", t)
                }
            })
        }
    }

    private fun updateUI(product: MenuModel) {
        productName.text = product.menu_name
        productVariant.text = product.variant
        productInformation.text = product.information
        productPrice.text = "Rp${product.price}"

        Glide.with(this)
            .load(product.picture)
            .placeholder(R.drawable.placeholder_image)
            .into(productImage)
    }
}

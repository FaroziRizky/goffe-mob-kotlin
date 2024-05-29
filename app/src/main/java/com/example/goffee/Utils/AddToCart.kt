package com.example.goffee.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CartService {

    fun addToCart(context: Context, id_menu: Int) {
        val token = TokenManager.getToken(context)
        if (token == null) {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val addToCartRequest = ApiService.addToCartRequest(id_menu)
        RetrofitInstance.api.addToCart("Bearer $token", addToCartRequest)
            .enqueue(object : Callback<ApiService.addToCartResponse> {
                override fun onResponse(
                    call: Call<ApiService.addToCartResponse>,
                    response: Response<ApiService.addToCartResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val addToCartResponse = response.body()
                        Toast.makeText(
                            context,
                            addToCartResponse?.message ?: "Item telah ditambahkan ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Gagal menambahkan item ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiService.addToCartResponse>, t: Throwable) {
                    Log.d("Error", "onFailure: $t")
                    Toast.makeText(context, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

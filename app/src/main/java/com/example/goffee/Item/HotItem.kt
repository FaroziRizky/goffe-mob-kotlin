package com.example.goffee.Item

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.Adapters.MenuHotAdapter
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotItem : Fragment() {
    private lateinit var itemHotRecyclerView: RecyclerView
    private lateinit var itemHotAdapter: MenuHotAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hot_item, container, false)
        itemHotRecyclerView = view.findViewById(R.id.hotRecyclerView)
        itemHotRecyclerView.layoutManager = GridLayoutManager(context, 2)

        fetchRecommendedHotProducts()

        return view
    }

    private fun fetchRecommendedHotProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.menuHot(token)
                .enqueue(object : Callback<ApiService.menuHotResponse> {
                    override fun onResponse(
                        call: Call<ApiService.menuHotResponse>,
                        response: Response<ApiService.menuHotResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                itemHotAdapter = MenuHotAdapter(it.values)
                                itemHotRecyclerView.adapter = itemHotAdapter
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to load recommended products",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.menuHotResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

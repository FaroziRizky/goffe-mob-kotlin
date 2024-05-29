package com.example.goffee.MainMenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.LoginPage
import com.example.goffee.MainPage
import com.example.goffee.Models.AkunModel
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : Fragment() {
    private lateinit var recommendedProductsRecyclerView: RecyclerView
    private lateinit var recommendedIceProductsRecyclerView: RecyclerView
    private lateinit var recommendedHotProductsRecyclerView: RecyclerView
    private lateinit var recommendedProductsAdapter: RecommendedProductsAdapter
    private lateinit var recommendedIceProductsAdapter: RecommendedIceProductsAdapter
    private lateinit var recommendedHotProductsAdapter: RecommendedHotProductsAdapter
    private lateinit var nama: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        recommendedProductsRecyclerView = view.findViewById(R.id.recommendedProductsRecyclerView)
        recommendedProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recommendedIceProductsRecyclerView =
            view.findViewById(R.id.recommendedIceProductsRecyclerView)
        recommendedIceProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recommendedHotProductsRecyclerView =
            view.findViewById(R.id.recommendedHotProductsRecyclerView)
        recommendedHotProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        nama = view.findViewById(R.id.user_name)



        fetchRecommendedProducts()
        fetchRecommendedIceProducts()
        fetchRecommendedHotProducts()
        fetchProfile()

        return view
    }

    private fun fetchRecommendedProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedProducts(token)
                .enqueue(object : Callback<ApiService.recomendedResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedResponse>,
                        response: Response<ApiService.recomendedResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recommendedProductsAdapter = RecommendedProductsAdapter(it.values)
                                recommendedProductsRecyclerView.adapter = recommendedProductsAdapter
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
                        call: Call<ApiService.recomendedResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedIceProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedIceProducts(token)
                .enqueue(object : Callback<ApiService.recomendedIceResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedIceResponse>,
                        response: Response<ApiService.recomendedIceResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recommendedIceProductsAdapter =
                                    RecommendedIceProductsAdapter(it.values)
                                recommendedIceProductsRecyclerView.adapter =
                                    recommendedIceProductsAdapter
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Sesi telah berakhir",
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().startActivity(Intent(requireContext(), LoginPage::class.java))

                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.recomendedIceResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedHotProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedHotProducts(token)
                .enqueue(object : Callback<ApiService.recomendedHotResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedHotResponse>,
                        response: Response<ApiService.recomendedHotResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recommendedHotProductsAdapter =
                                    RecommendedHotProductsAdapter(it.values)
                                recommendedHotProductsRecyclerView.adapter =
                                    recommendedHotProductsAdapter
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
                        call: Call<ApiService.recomendedHotResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchProfile() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.profile(token).enqueue(object : Callback<ApiService.profileResponse> {
                override fun onResponse(call: Call<ApiService.profileResponse>, response: Response<ApiService.profileResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            nama.setText(it.values[0].fullname)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to load profile data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiService.profileResponse>, t: Throwable) {
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

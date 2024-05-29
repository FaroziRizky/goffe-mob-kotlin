package com.example.goffee.Item

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.Adapters.MenuIceAdapter
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.LoginPage
import com.example.goffee.Models.MenuModel
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IceItem : Fragment() {
    private lateinit var itemIceRecyclerView: RecyclerView
    private lateinit var itemIceAdapter: MenuIceAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ice_item, container, false)
        itemIceRecyclerView = view.findViewById(R.id.iceRecyclerView)
        itemIceRecyclerView.layoutManager = GridLayoutManager(context, 2)

        fetchRecommendedIceProducts()

        return view
    }

    private fun fetchRecommendedIceProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.menuIce(token)
                .enqueue(object : Callback<ApiService.menuIceResponse> {
                    override fun onResponse(
                        call: Call<ApiService.menuIceResponse>,
                        response: Response<ApiService.menuIceResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                itemIceAdapter = MenuIceAdapter(it.values)
                                itemIceRecyclerView.adapter = itemIceAdapter
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
                        call: Call<ApiService.menuIceResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}

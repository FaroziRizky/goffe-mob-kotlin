package com.example.goffee.Order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goffee.Adapters.HistoryAdapter
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PendingFragment : Fragment() {

    private lateinit var recyclerViewPendingOrders: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noItem: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pending, container, false)

        noItem = view.findViewById(R.id.noItem)
        // Initialize RecyclerView and Adapter
        recyclerViewPendingOrders = view.findViewById(R.id.recyclerViewPendingOrders)
        recyclerViewPendingOrders.layoutManager = LinearLayoutManager(context)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchPendingOrders()
        }

        fetchPendingOrders()

        return view
    }

    private fun fetchPendingOrders() {
        swipeRefreshLayout.isRefreshing = true
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.processHistory(token)
                .enqueue(object : Callback<ApiService.HistoryResponse> {
                    override fun onResponse(
                        call: Call<ApiService.HistoryResponse>,
                        response: Response<ApiService.HistoryResponse>
                    ) {
                        swipeRefreshLayout.isRefreshing = false
                        if (response.isSuccessful) {
                            response.body()?.let {
                                Log.d("Error Cuy", "onResponse: ${it.values.size == 0}")
                                if (it.values.size == 0) {
                                    noItem.visibility = View.VISIBLE
                                    recyclerViewPendingOrders.visibility = View.GONE
                                }else{
                                    recyclerViewPendingOrders.visibility = View.VISIBLE
                                    historyAdapter = HistoryAdapter(it.values)
                                    recyclerViewPendingOrders.adapter = historyAdapter
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to load pending orders",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.HistoryResponse>,
                        t: Throwable
                    ) {
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "Token is null", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.goffee.MainMenu

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.EditPassword
import com.example.goffee.EditProfilePage
import com.example.goffee.LoginPage
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountPage : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewPhone: TextView
    private lateinit var btnLogout: TextView
    private lateinit var btnEditPassword: TextView
    private lateinit var btn_edit: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_account_page, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        textViewFullName= view.findViewById(R.id.textViewFullName)
        textViewPhone= view.findViewById(R.id.textViewPhone)
        textViewEmail= view.findViewById(R.id.textViewEmail)

        btnEditPassword = view.findViewById(R.id.btnEditPassword)
        btnLogout = view.findViewById(R.id.btnLogout)
        btn_edit = view.findViewById(R.id.btn_edit)

        setupSwipeToRefresh()

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        btnEditPassword.setOnClickListener {
            val intent = Intent(context, EditPassword::class.java)
            startActivity(intent)
        }

        fetchProfile()







        return view
    }
    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh action
            fetchProfile()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin Keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun logout() {
        context?.let { TokenManager.clearToken(it) }
        val intent = Intent(context, LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun fetchProfile() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.profile(token).enqueue(object :
                Callback<ApiService.profileResponse> {
                override fun onResponse(call: Call<ApiService.profileResponse>, response: Response<ApiService.profileResponse>) {
                    swipeRefreshLayout.isRefreshing = false
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val profile=it.values[0]
                            textViewFullName.setText(profile.fullname)
                            textViewEmail.setText(profile.email)
                            textViewPhone.setText(profile.phone)
                            btn_edit.setOnClickListener {
                                val intent = Intent(context, EditProfilePage::class.java).apply {
                                    putExtra("fullname", profile.fullname)
                                    putExtra("email", profile.email)
                                    putExtra("phone", profile.phone)
                                }
                                startActivity(intent)
                            }
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
                    swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
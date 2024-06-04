package com.example.goffee

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfilePage : AppCompatActivity() {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateProfile: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile_page)

        // Inisialisasi view
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile)
        btnBack = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        editTextFullName.setText(intent.getStringExtra("fullname"))
        editTextPhone.setText(intent.getStringExtra("phone"))
        editTextEmail.setText(intent.getStringExtra("email"))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonUpdateProfile.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        val token = TokenManager.getToken(this)

        // Ambil data dari input fields
        val fullName = editTextFullName.text.toString()
        val phone = editTextPhone.text.toString()
        val email = editTextEmail.text.toString()

        if (token != null) {
            val apiService = RetrofitInstance.create(token)

            val requestBody = mapOf(
                "fullname" to fullName,
                "phone" to phone,
                "email" to email
            )

            val editProfile: Call<Void?>? = apiService.updateUserProfile(token, requestBody)

            if (editProfile != null) {
                editProfile.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@EditProfilePage, "Berhasil Memperbarui Profile", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(this@EditProfilePage, "Gagal Memperbarui Profile", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Toast.makeText(this@EditProfilePage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}

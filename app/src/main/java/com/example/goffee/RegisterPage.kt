package com.example.goffee

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPage : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        // Set up toolbar
        val toolbar = findViewById<ImageButton>(R.id.btn_back)
        toolbar.setOnClickListener { onBackPressed() }

        // Set up view insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the register button and fields
        val fullnameEditText = findViewById<EditText>(R.id.editTextFullname)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmationPasswordEditText = findViewById<EditText>(R.id.editTextConfirmationPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val apiMessageContainer = findViewById<LinearLayout>(R.id.apiMessageContainer)
        val apiMessageText = findViewById<TextView>(R.id.apiMessageText)

        registerButton.setOnClickListener {

            // Get data from input fields
            val fullName = fullnameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confPassword = confirmationPasswordEditText.text.toString()

            val apiService = RetrofitInstance.api

            val requestBody = mapOf(
                "fullname" to fullName,
                "phone" to phone,
                "email" to email,
                "password" to password,
                "confirmation_password" to confPassword
            )

            Log.d("RegisterPage", requestBody.toString())

            val register: Call<Void?>? = apiService!!.register(requestBody)

            if (register != null) {
                register.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                Toast.makeText(
                                    this@RegisterPage,
                                    "Registrasi berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBackPressed()
                            } else {
                                apiMessageText.text = "Please check your input data"
                                apiMessageContainer.visibility = View.VISIBLE
                                handler.postDelayed({
                                    apiMessageContainer.visibility = View.GONE
                                }, 1000)
                            }
                        } else {
                            Toast.makeText(
                                this@RegisterPage,
                                "Gagal Membuat akun: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {

                        apiMessageText.text = "Layanan tidak tersedia"
                        apiMessageContainer.visibility = View.VISIBLE
                        handler.postDelayed({
                            apiMessageContainer.visibility = View.GONE
                        }, 1000)
                    }
                })
            }
        }
    }
}

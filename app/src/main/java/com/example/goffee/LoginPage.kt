package com.example.goffee

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.Models.LoginRequest
import com.example.goffee.Models.LoginResponse
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var showHideButton: ImageButton
    private lateinit var btnLogin: TextView
    private lateinit var btnRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        showHideButton = findViewById(R.id.btnShowHidePassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        emailEditText.setText("budi@example.com")
        passwordEditText.setText("1")

        btnLogin.setOnClickListener {
            login()
        }
        btnRegister.setOnClickListener {
            val intent = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(intent)
        }

        showHideButton.setOnClickListener {
            togglePasswordVisibility()
        }

    }

    private fun togglePasswordVisibility() {
        if (passwordEditText.transformationMethod is PasswordTransformationMethod) {
            // Show password
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            showHideButton.setImageResource(R.drawable.ic_hide_password)
        } else {
            // Hide password
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            showHideButton.setImageResource(R.drawable.ic_show_password)
        }
        // Move the cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.text.length)
    }
    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(email, password)
        RetrofitInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                val statusCode = response.code()
                if (statusCode == 200 && loginResponse != null && loginResponse.token!=null) {
                    TokenManager.saveToken(this@LoginPage, loginResponse.token)
                    Toast.makeText(this@LoginPage, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginPage, MainPage::class.java))
                    finish()
                    // Save token and navigate to next screen
                } else {
                    Toast.makeText(this@LoginPage, "Cek Email dan Password!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("ErrorCuy", "onResponse: $t, $call")
                Toast.makeText(this@LoginPage, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

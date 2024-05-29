package com.example.goffee

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPassword : AppCompatActivity() {
    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confPasswordEditText: EditText
    private lateinit var updatePasswordButton: TextView
    private lateinit var btn_back: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        oldPasswordEditText = findViewById(R.id.editTextOldPassword)
        newPasswordEditText = findViewById(R.id.editTextNewPassword)
        confPasswordEditText = findViewById(R.id.editTextConfPassword)
        updatePasswordButton = findViewById(R.id.btnEditPassword)
        btn_back = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            onBackPressed()
        }

        updatePasswordButton.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val oldPassword = oldPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()
        val confPassword = confPasswordEditText.text.toString()

        if (newPassword != confPassword) {
            Toast.makeText(
                this,
                "Password baru & konfirmasi tidak cocok",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val token = TokenManager.getToken(this)
        if (token != null) {
            val requestBody = mapOf(
                "old_password" to oldPassword,
                "password" to newPassword
            )
            RetrofitInstance.api.updatePassword(token, requestBody)
                ?.enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@EditPassword,
                                "Password berhasil diperbarui",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                            finish()
                        } else {
                            Log.d("Error Cuy", "onResponse: ${response.code()}, ${response.body()}")
                            Toast.makeText(
                                this@EditPassword,
                                "Periksa Password Lama!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Toast.makeText(
                            this@EditPassword,
                            "Terjadi kesalahan: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }
}

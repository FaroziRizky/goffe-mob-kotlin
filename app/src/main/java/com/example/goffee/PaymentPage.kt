package com.example.goffee

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentPage : AppCompatActivity() {

    private lateinit var totalPaymentTextView: TextView
    private lateinit var bankNameTextView: TextView
    private lateinit var bankAccountTextView: TextView
    private lateinit var copyTotalPaymentButton: Button
    private lateinit var copyBankAccountButton: Button
    private lateinit var confirmPaymentButton: Button
    private lateinit var canelOrderButton: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_page)

        totalPaymentTextView = findViewById(R.id.total_payment)
        bankNameTextView = findViewById(R.id.bank_name)
        bankAccountTextView = findViewById(R.id.bank_account)
        copyTotalPaymentButton = findViewById(R.id.copy_total_payment)
        copyBankAccountButton = findViewById(R.id.copy_bank_account)
        confirmPaymentButton = findViewById(R.id.confirm_payment)
        canelOrderButton = findViewById(R.id.cancel_order)
        btnBack = findViewById(R.id.btn_back)

        val total = intent.getStringExtra("total")
        val bank_name = intent.getStringExtra("bank_name")
        val bank_account = intent.getStringExtra("bank_account")
        val id_history = intent.getStringExtra("id_history")

        Log.d("ErrorCuy", "onCreate: $id_history")

        // Set example data
        totalPaymentTextView.text = "Rp. " + PriceFormatter.format(total.toString())
        bankNameTextView.text = bank_name
        bankAccountTextView.text = bank_account

        copyTotalPaymentButton.setOnClickListener {
            if (total != null) {
                copyToClipboard(total, "Total pembayaran berhasil disalin")
            }
        }

        copyBankAccountButton.setOnClickListener {
            if (bank_account != null) {
                copyToClipboard(bank_account, "No. Rekening berhasil disalin")
            }
        }

        confirmPaymentButton.setOnClickListener {
            val token = TokenManager.getToken(this)
            if (token != null) {
                if (id_history != null) {
                    RetrofitInstance.api.confPay(token, id_history.toInt())
                        .enqueue(object : Callback<ApiService.confPayResponse> {
                            override fun onResponse(
                                call: Call<ApiService.confPayResponse>,
                                response: Response<ApiService.confPayResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Pembayaran dikonfirmasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Tidak bisa mengkonfirmasi pembayaran",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiService.confPayResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@PaymentPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        canelOrderButton.setOnClickListener {
            val token = TokenManager.getToken(this)
            if (token != null) {
                if (id_history != null) {
                    RetrofitInstance.api.cancelOrder(token, id_history.toInt())
                        .enqueue(object : Callback<ApiService.cancelOrderResponse> {
                            override fun onResponse(
                                call: Call<ApiService.cancelOrderResponse>,
                                response: Response<ApiService.cancelOrderResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Pesanan dibatalkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@PaymentPage,
                                        "Tidak bisa membatalkan pesanan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiService.cancelOrderResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@PaymentPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun copyToClipboard(text: String, message: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

package com.example.goffee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.Adapters.MenuOrderAdapter
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.Utils.DateTimeFormatter
import com.example.goffee.api.RetrofitInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailOrderPage : AppCompatActivity() {

    private var totalPay = 0
    private lateinit var total: TextView
    private lateinit var orderedAtTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var userNotesTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var adminNotesTextView: TextView
    private lateinit var finishedAtTextView: TextView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var addressContainer: LinearLayout
    private lateinit var adminNotesContainer: LinearLayout
    private lateinit var finishContainer: LinearLayout
    private lateinit var buttonContainer: LinearLayout
    private lateinit var btn_back: ImageButton
    private lateinit var btn_payment_info: Button
    private lateinit var confirmPaymentButton: Button
    private lateinit var cancelOrderButton: Button

    private lateinit var menuAdapter: MenuOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_order_page)

        // Initialize Views
        total = findViewById(R.id.total)
        orderedAtTextView = findViewById(R.id.ordered_at)
        statusTextView = findViewById(R.id.status)
        userNotesTextView = findViewById(R.id.user_notes)
        addressTextView = findViewById(R.id.address)
        adminNotesTextView = findViewById(R.id.admin_notes)
        finishedAtTextView = findViewById(R.id.finished_at)
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu)
        addressContainer = findViewById(R.id.addressContainer)
        adminNotesContainer = findViewById(R.id.adminNotesContainer)
        finishContainer = findViewById(R.id.finishContainer)
        btn_back = findViewById(R.id.btn_back)
        btn_payment_info = findViewById(R.id.btn_payment_info)
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton)
        cancelOrderButton = findViewById(R.id.cancelOrderButton)
        buttonContainer = findViewById(R.id.buttonContainer)


        btn_back.setOnClickListener {
            onBackPressed()
            finish()
        }

        // Parse data from intent or API response
        val id_history = intent.getStringExtra("id_history")

        btn_payment_info.setOnClickListener {
            if (id_history != null) {
                fetchPayment(id_history.toInt())
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
                                        this@DetailOrderPage,
                                        "Pembayaran dikonfirmasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@DetailOrderPage,
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
                                    this@DetailOrderPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        cancelOrderButton.setOnClickListener {
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
                                        this@DetailOrderPage,
                                        "Pesanan dibatalkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@DetailOrderPage,
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
                                    this@DetailOrderPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        if (id_history != null) {
            fetchHistory(id_history.toInt())
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }



    private fun fetchHistory(id_history: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.detailHistory(token, id_history)
                .enqueue(object : Callback<ApiService.detailResponse> {
                    override fun onResponse(
                        call: Call<ApiService.detailResponse>,
                        response: Response<ApiService.detailResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { detailResponse ->
                                val history = detailResponse.values.history
                                val menu = detailResponse.values.menu

                                orderedAtTextView.text =
                                    DateTimeFormatter.formatDateTime(history.ordered_at)
                                total.text = "Rp. "+ PriceFormatter.format(history.total.toString())
                                totalPay = history.total


                                when (history.status) {
                                    0 -> {
                                        statusTextView.text = "Belum Bayar"
                                        buttonContainer.visibility=View.VISIBLE
                                        btn_payment_info.visibility= View.VISIBLE
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.yellow
                                            )
                                        )
                                    }

                                    1 -> {
                                        statusTextView.text = "Dibatalkan User"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.red
                                            )
                                        )
                                    }

                                    2 -> {
                                        statusTextView.text = "Dibatalkan Admin"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.red_dark
                                            )
                                        )
                                    }

                                    3 -> {
                                        statusTextView.text = "Telah Dibayar"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.blue
                                            )
                                        )
                                    }

                                    4 -> {
                                        statusTextView.text = "Dalam Proses"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.orange
                                            )
                                        )
                                    }

                                    5 -> {
                                        statusTextView.text = "Siap/diantar"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.green
                                            )
                                        )
                                    }

                                    6 -> {
                                        statusTextView.text = "Selesai"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.green_dark
                                            )
                                        )
                                    }

                                    else -> {
                                        statusTextView.text = ""
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailOrderPage,
                                                R.color.black
                                            )
                                        )
                                    }
                                }

                                when (history.user_notes) {
                                    "" -> userNotesTextView.text = "-"
                                    null -> userNotesTextView.text = "-"
                                    "null" -> userNotesTextView.text = "-"
                                    else -> userNotesTextView.text = history.user_notes
                                }
                                when (history.address) {
                                    "" -> addressContainer.visibility = View.GONE
                                    null -> addressContainer.visibility = View.GONE
                                    "null" -> addressContainer.visibility = View.GONE
                                    else -> {
                                        addressContainer.visibility = View.VISIBLE
                                        addressTextView.text = history.address
                                    }
                                }
                                when (history.admin_notes) {
                                    "" -> adminNotesContainer.visibility = View.GONE
                                    null -> adminNotesContainer.visibility = View.GONE
                                    "null" -> adminNotesContainer.visibility = View.GONE
                                    else -> {
                                        adminNotesContainer.visibility = View.VISIBLE
                                        adminNotesTextView.text = history.admin_notes
                                    }
                                }
                                when (history.finished_at) {
                                    "" -> finishContainer.visibility = View.GONE
                                    null -> finishContainer.visibility = View.GONE
                                    "null" -> finishContainer.visibility = View.GONE
                                    else -> {
                                        finishContainer.visibility = View.VISIBLE
                                        finishedAtTextView.text = history.finished_at
                                    }
                                }


                                recyclerViewMenu.layoutManager =
                                    LinearLayoutManager(this@DetailOrderPage)
                                menuAdapter = MenuOrderAdapter(menu)
                                recyclerViewMenu.adapter = menuAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@DetailOrderPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.detailResponse>, t: Throwable) {
                        Toast.makeText(
                            this@DetailOrderPage,
                            "An error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun fetchPayment(id_history: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.infoPayment(token)
                .enqueue(object : Callback<ApiService.infoPaymentResponse> {
                    override fun onResponse(
                        call: Call<ApiService.infoPaymentResponse>,
                        response: Response<ApiService.infoPaymentResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { infoPaymentDetail ->
                                val intent =
                                    Intent(this@DetailOrderPage, PaymentPage::class.java).apply {
                                        putExtra("total", totalPay.toString())
                                        putExtra("bank_name", infoPaymentDetail.values.bank_name)
                                        putExtra(
                                            "bank_account",
                                            infoPaymentDetail.values.bank_account
                                        )
                                        putExtra("id_history", id_history.toString())
                                        Log.d("Error Cuy", "onResponse: $id_history")
                                    }
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@DetailOrderPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.infoPaymentResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@DetailOrderPage,
                            "An error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }
}

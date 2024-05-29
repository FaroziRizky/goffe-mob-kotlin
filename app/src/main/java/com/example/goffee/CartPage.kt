package com.example.goffee

import CartAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.ApiService.ApiService
import com.google.android.material.button.MaterialButtonToggleGroup
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.Models.CartItem
import com.example.goffee.Models.CartResponse
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartPage : AppCompatActivity(), CartAdapter.OnAmountChangeListener {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalAmount: TextView
    private lateinit var shppingAmount: TextView
    private lateinit var emptyCartText: TextView
    private lateinit var btn_back: ImageButton
    private lateinit var checkoutButton: TextView
    private lateinit var notes: TextView
    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var alamatEditText: EditText
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var shippingContainer: LinearLayout
    private lateinit var totalPriceLayout: RelativeLayout
    private var shippingCost = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_page)

        emptyCartText = findViewById(R.id.emptyCartText)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmount = findViewById(R.id.totalAmount)
        shppingAmount = findViewById(R.id.shppingAmount)
        checkoutButton = findViewById(R.id.checkoutButton)
        btn_back = findViewById(R.id.btn_back)
        alamatEditText = findViewById(R.id.alamat)
        notes = findViewById(R.id.notes)
        toggleGroup = findViewById(R.id.toggleGroup)
        shippingContainer = findViewById(R.id.shippingContainer)
        totalPriceLayout = findViewById(R.id.totalPriceLayout)

        checkoutButton.setOnClickListener {
            checkout()
        }


        btn_back.setOnClickListener {
            onBackPressed()
        }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.button_delivery && isChecked) {
                shppingAmount.text = "Rp 5000"
                shippingCost = 5000
                updateTotalAmount(cartItems)
                alamatEditText.visibility = View.VISIBLE
                shippingContainer.visibility = View.VISIBLE
                val params = totalPriceLayout.layoutParams as LinearLayout.LayoutParams
                params.weight = 1.24f
                totalPriceLayout.layoutParams = params
            } else if (checkedId == R.id.button_pick_up && isChecked) {
                shppingAmount.text = ""
                shippingCost = 0
                alamatEditText.text = null
                updateTotalAmount(cartItems)
                alamatEditText.visibility = View.GONE
                shippingContainer.visibility = View.GONE
                val params = totalPriceLayout.layoutParams as LinearLayout.LayoutParams
                params.weight = 1f
                totalPriceLayout.layoutParams = params
            }
        }

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkout() {
        val token = TokenManager.getToken(this)
        var address = alamatEditText.text.toString()
        var notes = notes.text.toString()
        var reqBody = ApiService.checkoutRequest(
            address = address,
            user_notes = notes
        )
        if (token != null) {
            RetrofitInstance.api.checkout(token, reqBody)
                .enqueue(object : Callback<ApiService.checkoutResponse> {
                    override fun onResponse(
                        call: Call<ApiService.checkoutResponse>,
                        response: Response<ApiService.checkoutResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                val intent = Intent(this@CartPage, PaymentPage::class.java).apply {
                                    putExtra("total", it.total.toString())
                                    putExtra("bank_name", it.bank_name)
                                    putExtra("bank_account", it.bank_account)
                                    putExtra("id_history", it.iHistory.toString())
                                }
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@CartPage,
                                "Tidak ada menu di keranjang",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.checkoutResponse>, t: Throwable) {
                        Toast.makeText(this@CartPage, "Layanan tidak tersedia", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    private fun fetchCartItems() {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getCartItems(token)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                cartItems = it.values[0].cart_item.toMutableList()
                                if (cartItems.isEmpty()) {
                                    // Tampilkan TextView jika keranjang kosong
                                    emptyCartText.visibility = View.VISIBLE
                                    cartRecyclerView.visibility = View.GONE
                                    toggleGroup.visibility = View.GONE
                                } else {
                                    // Sembunyikan TextView jika keranjang tidak kosong
                                    emptyCartText.visibility = View.GONE
                                    cartRecyclerView.visibility = View.VISIBLE
                                    toggleGroup.visibility = View.VISIBLE
                                    cartRecyclerView.adapter = CartAdapter(cartItems, this@CartPage)
                                    updateTotalAmount(cartItems)
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@CartPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        Toast.makeText(this@CartPage, "An error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    override fun onAmountChanged() {
        updateTotalAmount(cartItems)
    }

    private fun updateTotalAmount(cartItems: List<CartItem>) {
        var total = 0
        for (item in cartItems) {
            total += item.amount * item.price + shippingCost
        }
        totalAmount.text = "Rp $total"
        if (cartItems.isEmpty()) {
            // Tampilkan TextView jika keranjang kosong
            emptyCartText.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            toggleGroup.visibility = View.GONE
        } else {
            // Sembunyikan TextView jika keranjang tidak kosong
            emptyCartText.visibility = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
            toggleGroup.visibility = View.VISIBLE
        }
    }
}

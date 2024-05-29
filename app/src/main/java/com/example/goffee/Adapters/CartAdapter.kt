import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goffee.ApiService.ApiService
import com.example.goffee.ApiService.TokenManager
import com.example.goffee.Models.CartItem
import com.example.goffee.R
import com.example.goffee.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(private val cartItems: MutableList<CartItem>, private val amountChangeListener: OnAmountChangeListener) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnAmountChangeListener {
        fun onAmountChanged()
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemImage: ImageView = view.findViewById(R.id.cartItemImage)
        val cartItemName: TextView = view.findViewById(R.id.cartItemName)
        val cartItemVariant: TextView = view.findViewById(R.id.cartItemVariant)
        val cartItemAmount: TextView = view.findViewById(R.id.cartItemAmount)
        val cartItemPrice: TextView = view.findViewById(R.id.cartItemPrice)
        val decreaseAmount: ImageButton = view.findViewById(R.id.decreaseAmount)
        val increaseAmount: ImageButton = view.findViewById(R.id.increaseAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.cartItemName.text = cartItem.menu_name
        holder.cartItemVariant.text = cartItem.variant
        holder.cartItemPrice.text = "Rp. ${cartItem.price}"
        holder.cartItemAmount.text = cartItem.amount.toString()

        var amount = cartItem.amount

        Glide.with(holder.itemView)
            .load(cartItem.picture)
            .into(holder.cartItemImage)

        holder.decreaseAmount.setOnClickListener {
            if (amount > 1) {
                amount -= 1
                holder.cartItemAmount.text = amount.toString()
                updateCartItemAmount(holder.itemView.context, cartItem.id_cart_item, amount)
                cartItem.amount = amount
                amountChangeListener.onAmountChanged()
            } else {
                showDeleteConfirmationDialog(holder.itemView.context, position)
            }
        }

        holder.increaseAmount.setOnClickListener {
            amount += 1
            holder.cartItemAmount.text = amount.toString()
            updateCartItemAmount(holder.itemView.context, cartItem.id_cart_item, amount)
            cartItem.amount = amount
            amountChangeListener.onAmountChanged()
        }
    }

    private fun updateCartItemAmount(context: Context, idCartItem: Int, amount: Int) {
        val token = TokenManager.getToken(context)
        val requestBody = mapOf("id_cart_item" to idCartItem, "amount" to amount)
        if (token != null) {
            RetrofitInstance.api.setCartItemAmount(token, requestBody)
                .enqueue(object : Callback<ApiService.setCartItemAmountResponse> {
                    override fun onResponse(call: Call<ApiService.setCartItemAmountResponse>, response: Response<ApiService.setCartItemAmountResponse>) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            return
                        } else {
                            Toast.makeText(context, "Gagal memperbarui jumlah", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.setCartItemAmountResponse>, t: Throwable) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun deleteCartItem(context: Context, idCartItem: Int, position: Int) {
        val token = TokenManager.getToken(context)

        if (token != null) {
            RetrofitInstance.api.deleteItemCart(idCartItem, token)
                .enqueue(object : Callback<ApiService.deleteItemCartResponse> {
                    override fun onResponse(call: Call<ApiService.deleteItemCartResponse>, response: Response<ApiService.deleteItemCartResponse>) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            cartItems.removeAt(position)
                            notifyItemRemoved(position)
                            amountChangeListener.onAmountChanged()
                        } else {
                            Toast.makeText(context, "Gagal menghapus menu", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.deleteItemCartResponse>, t: Throwable) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus menu ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                val idCartItem = cartItems[position].id_cart_item
                deleteCartItem(context, idCartItem, position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun getItemCount(): Int = cartItems.size
}

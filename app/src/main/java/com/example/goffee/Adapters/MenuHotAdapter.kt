package com.example.goffee.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goffee.Utils.CartService
import com.example.goffee.DetailItemPage
import com.example.goffee.Models.MenuModel
import com.example.goffee.R

class MenuHotAdapter(private val products: List<MenuModel>) :
    RecyclerView.Adapter<MenuHotAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val addToCartIcon: ImageButton = view.findViewById(R.id.addToCartIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.menu_name
        holder.productPrice.text = "Rp ${product.price}"
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailItemPage::class.java).apply {
                putExtra("id_menu", product.id_menu)
            }
            holder.itemView.context.startActivity(intent)

        }
        holder.addToCartIcon.setOnClickListener {
            CartService.addToCart(holder.itemView.context, product.id_menu)
        }

        Glide.with(holder.itemView)
            .load(product.picture)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int = products.size
}

// MenuOrderAdapter.kt
package com.example.goffee.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.Models.MenuOrder
import com.example.goffee.R

class MenuOrderAdapter(private val menuOrders: List<MenuOrder>) :
    RecyclerView.Adapter<MenuOrderAdapter.MenuOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_order, parent, false)
        return MenuOrderViewHolder(view)
    }
    override fun onBindViewHolder(holder: MenuOrderViewHolder, position: Int) {
        val menuOrder = menuOrders[position]
        Log.d("Error Cuy", "onBindViewHolder: ${menuOrder.menu_name}")
        holder.cartItemName.text = menuOrder.menu_name
        holder.cartItemVariant.text = menuOrder.variant
        holder.cartItemPrice.text = "Rp ${formatPrice(menuOrder.price.toLong())}"
        holder.cartItemAmount.text = menuOrder.amount.toString()
    }

    override fun getItemCount(): Int = menuOrders.size

    private fun formatPrice(price: Long): String {
        return String.format("%,d", price).replace(',', '.')
    }

    class MenuOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
        val cartItemVariant: TextView = itemView.findViewById(R.id.cartItemVariant)
        val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        val cartItemAmount: TextView = itemView.findViewById(R.id.cartItemAmount)
    }
}

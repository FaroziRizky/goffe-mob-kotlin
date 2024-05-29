package com.example.goffee.Adapters

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.goffee.DetailOrderPage
import com.example.goffee.Models.Order
import com.example.goffee.R
import com.example.goffee.Utils.DateTimeFormatter


class HistoryAdapter(private val orders: List<Order>) : RecyclerView.Adapter<HistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.total.text = "Rp " + formatPrice(order.total.toString())
        holder.orderedAt.text =  DateTimeFormatter.formatDateTime(order.ordered_at) ?: "Tanggal tidak valid"

        val statusBackground = holder.status.background as GradientDrawable

        when (order.status) {
            0 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.yellow))
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                holder.status.text = "Belum Bayar"
            }
            1 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.red))
                holder.status.text = "Dibatalkan User"
            }
            2 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.red_dark))
                holder.status.text = "Dibatalkan Admin"
            }
            3 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.blue))
                holder.status.text = "Telah Dibayar"
            }
            4 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.orange))
                holder.status.text = "Dalam Proses"
            }
            5 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.green))
                holder.status.text = "Sudah Siap/Diantar"
            }
            6 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.green_dark))
                holder.status.text = "Selesai"
            }
            else -> statusBackground.setColor(holder.itemView.context.getColor(R.color.black)) // Default
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailOrderPage::class.java).apply {
                putExtra("id_history", order.id_history.toString())
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    private fun formatPrice(price: String): String {
        return try {
            val number = price.replace("[^\\d]".toRegex(), "").toLong()
            String.format("%,d", number).replace(',', '.')
        } catch (e: NumberFormatException) {
            price
        }
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val total: TextView = itemView.findViewById(R.id.total)
        val orderedAt: TextView = itemView.findViewById(R.id.ordered_at)
        val status: TextView = itemView.findViewById(R.id.status)
    }
}

// RVGlobalAdapter.kt
package com.example.stockmarketshowdown.ui.globe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.R
import java.text.SimpleDateFormat
import java.util.*

class RVGlobalAdapter(private var dataList: MutableList<GlobalData>) : RecyclerView.Adapter<RVGlobalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.global_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newData: List<GlobalData>) {
        dataList.clear()
        dataList.addAll(newData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTicker: TextView = itemView.findViewById(R.id.text_ticker)
        private val textQuantity: TextView = itemView.findViewById(R.id.text_quantity)
        private val textAction: TextView = itemView.findViewById(R.id.text_action)
        private val textTimestamp: TextView = itemView.findViewById(R.id.text_timestamp)

        fun bind(data: GlobalData) {
            textTicker.text = data.ticker
            textQuantity.text = data.quantity.toString()
            textAction.text = data.action
            val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(data.timestamp))
            textTimestamp.text = formattedTime
        }
    }

    data class GlobalData(
        val ticker: String,
        val quantity: Int,
        val action: String,
        val timestamp: Long
    )
}
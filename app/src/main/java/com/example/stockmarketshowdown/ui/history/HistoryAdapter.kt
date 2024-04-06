package com.example.stockmarketshowdown.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.databinding.HistoryRowBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    ListAdapter<TransactionHistory, HistoryAdapter.VH>(Diff()) {

    class Diff : DiffUtil.ItemCallback<TransactionHistory>() {
        override fun areItemsTheSame(oldItem: TransactionHistory, newItem: TransactionHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransactionHistory, newItem: TransactionHistory): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.TradeCompany == newItem.TradeCompany
                    && oldItem.TradeType == newItem.TradeType
                    && oldItem.TradeValue == newItem.TradeValue
                    && oldItem.TradeDate == newItem.TradeDate
        }
    }

    inner class VH(private val rowBinding: HistoryRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val transaction = viewModel.getTransaction(position)
            holder.rowBinding.rowCompany.text = transaction.TradeCompany
            holder.rowBinding.rowType.text = transaction.TradeType
            holder.rowBinding.rowValue.text = "$"+transaction.TradeValue.toString()
            var odt = OffsetDateTime.parse(transaction.TradeDate.toString())
            var dtf = DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH)
            holder.rowBinding.rowDate.text = dtf.format(odt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = HistoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}
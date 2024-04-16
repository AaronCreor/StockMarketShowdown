package com.example.stockmarketshowdown.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.databinding.HistoryRowBinding
import com.example.stockmarketshowdown.model.TransactionHistory
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    ListAdapter<TransactionHistory, HistoryAdapter.VH>(Diff()) {

    class Diff : DiffUtil.ItemCallback<TransactionHistory>() {
        override fun areItemsTheSame(oldItem: TransactionHistory, newItem: TransactionHistory): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: TransactionHistory, newItem: TransactionHistory): Boolean {
            return oldItem.transactionId == newItem.transactionId
                    && oldItem.tradeCompany == newItem.tradeCompany
                    && oldItem.tradeType == newItem.tradeType
                    && oldItem.tradeValue == newItem.tradeValue
                    && oldItem.tradeDate == newItem.tradeDate
        }
    }

    inner class VH(private val rowBinding: HistoryRowBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(holder: VH, position: Int) {
            val transaction = viewModel.getTransaction(position)
            holder.rowBinding.rowCompany.text = transaction.tradeCompany
            holder.rowBinding.rowType.text = transaction.tradeType
            holder.rowBinding.rowValue.text = "$"+transaction.tradeValue.toString()
            var outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            val formattedDate = outputFormat.format(transaction.tradeDate)
            holder.rowBinding.rowDate.text = formattedDate
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
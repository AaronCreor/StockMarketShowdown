package com.example.stockmarketshowdown.ui.history

import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {

    private val history = Transactions.history

    fun getTransaction(position: Int): TransactionHistory {
        return history[position]
    }


}
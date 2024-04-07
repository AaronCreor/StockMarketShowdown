package com.example.stockmarketshowdown.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class SortColumn {
    COMPANY,
    TYPE,
    DATE,
    VALUE
}

data class SortInfo(val sortColumn: SortColumn, val ascending: Boolean)
class HistoryViewModel : ViewModel() {

    private val history = Transactions.history

    private var historyList = MutableLiveData<List<TransactionHistory>>()

    private var sortInfo = MutableLiveData(SortInfo(SortColumn.DATE, true))

    fun getTransaction(position: Int): TransactionHistory {
        return history[position]
    }

    fun getTransactions(): List<TransactionHistory> {
        return history
    }

    fun observeHistoryList(): LiveData<List<TransactionHistory>> {
        return historyList
    }

    fun observeSortInfo(): LiveData<SortInfo> {
        return sortInfo
    }

    fun sortInfoClick(sortColumn: SortColumn, resultListener: () -> Unit) {
        if (sortInfo.value != null) {
            if (sortInfo.value!!.sortColumn == sortColumn) {
                // flip it
                sortInfo.postValue(SortInfo(sortColumn, !sortInfo.value!!.ascending))
            } else {
                sortInfo.postValue(SortInfo(sortColumn, sortInfo.value!!.ascending))
            }
        }
    }


}
package com.example.stockmarketshowdown.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private var sortInfo = MutableLiveData<SortInfo>().apply {
        value = SortInfo(SortColumn.DATE, false)
    }

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

    private fun sortList() : List<TransactionHistory> {
        if (sortInfo.value?.ascending == true) {
            when (sortInfo.value?.sortColumn) {
                SortColumn.COMPANY -> return history.sortedBy { it.TradeCompany.toString() }
                SortColumn.TYPE -> return history.sortedBy { it.TradeType.toString() }
                SortColumn.DATE -> return history.sortedBy { it.TradeDate }
                SortColumn.VALUE -> return history.sortedBy { it.TradeValue }
                null -> return history
            }
        } else {
            when (sortInfo.value?.sortColumn) {
                SortColumn.COMPANY -> return history.sortedByDescending { it.TradeCompany.toString() }
                SortColumn.TYPE -> return history.sortedByDescending { it.TradeType.toString() }
                SortColumn.DATE -> return history.sortedByDescending { it.TradeDate }
                SortColumn.VALUE -> return history.sortedByDescending { it.TradeValue }
                null -> return history
            }
        }
    }

    private var historyList = MediatorLiveData<List<TransactionHistory>>().apply {
        addSource(sortInfo) {
            value = sortList()
        }
        value = history
    }


}
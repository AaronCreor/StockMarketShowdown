package com.example.stockmarketshowdown.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.model.TransactionHistory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

enum class SortColumn {
    COMPANY,
    TYPE,
    DATE,
    VALUE
}

data class SortInfo(val sortColumn: SortColumn, val ascending: Boolean)
class HistoryViewModel : ViewModel() {

    private val history = MutableLiveData<List<TransactionHistory>>()

    private val sms = SMS()

    private var sortInfo = MutableLiveData<SortInfo>().apply {
        value = SortInfo(SortColumn.DATE, false)
    }

    fun getTransaction(position: Int): TransactionHistory {
        return history.value!![position]
    }

    suspend fun fetchTransactionHistory(resultListener: () -> Unit) {
        val userID = Firebase.auth.currentUser?.uid
        history.postValue(sms.getUserTransactionHistory(userID!!))
        resultListener.invoke()
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
        if (history.value != null) {
            if (sortInfo.value?.ascending == true) {
                when (sortInfo.value?.sortColumn) {
                    SortColumn.COMPANY -> return history?.value.sortedBy { it.tradeCompany }
                    SortColumn.TYPE -> return history.value!!.sortedBy { it.tradeType }
                    SortColumn.DATE -> return history.value!!.sortedBy { it.tradeDate }
                    SortColumn.VALUE -> return history.value!!.sortedBy { it.tradeValue }
                    null -> return history.value!!
                }
            } else {
                when (sortInfo.value?.sortColumn) {
                    SortColumn.COMPANY -> return history.value!!.sortedByDescending { it.tradeCompany }
                    SortColumn.TYPE -> return history.value!!.sortedByDescending { it.tradeType }
                    SortColumn.DATE -> return history.value!!.sortedByDescending { it.tradeDate }
                    SortColumn.VALUE -> return history.value!!.sortedByDescending { it.tradeValue }
                    null -> return history.value!!
                }
            }
        } else {
            return List<TransactionHistory>()
        }
    }

    private var historyList = MediatorLiveData<List<TransactionHistory>>().apply {
        addSource(sortInfo) {
            value = sortList()
        }
        value = history.value
    }


}
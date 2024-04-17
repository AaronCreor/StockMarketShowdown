package com.example.stockmarketshowdown.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
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
        Log.d("FETCH", sortInfo.value!!.toString())
        history.postValue(sms.getUserTransactionHistory(userID!!, sortInfo.value!!))
        resultListener.invoke()
    }

    fun observeHistory(): LiveData<List<TransactionHistory>> {
        return history
    }

    fun observeSortInfo(): LiveData<SortInfo> {
        return sortInfo
    }

    suspend fun sortInfoClick(sortColumn: SortColumn, resultListener: () -> Unit) {
        Log.d("sortInfo", sortColumn.toString())
        if (sortInfo.value != null) {
            if (sortInfo.value!!.sortColumn == sortColumn) {
                // flip it
                sortInfo.value = SortInfo(sortColumn, !sortInfo.value!!.ascending)
            } else {
                sortInfo.value = SortInfo(sortColumn, sortInfo.value!!.ascending)
            }
        }
        fetchTransactionHistory(resultListener)
    }
}
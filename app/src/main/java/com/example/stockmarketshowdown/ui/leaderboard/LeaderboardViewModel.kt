package com.example.stockmarketshowdown.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.model.LeaderboardEntry

class LeaderboardViewModel : ViewModel() {

    private val leaderboardData = LeaderboardData.leaderboard.sortedByDescending { it.score }

    private val sms = SMS()

    private var leaderboardList = MutableLiveData<List<LeaderboardEntry>>()

    fun getPosition(position: Int): LeaderboardEntry {
        return leaderboardList.value?.get(position)!!
    }

    suspend fun fetchLeaderboardData(resultListener: () -> Unit) {
//        Fetch from DB in this case.
        leaderboardList.postValue(sms.getTop10Scores())
        resultListener.invoke()
    }

    fun observeLeaderboardList(): LiveData<List<LeaderboardEntry>> {
        return leaderboardList
    }
}
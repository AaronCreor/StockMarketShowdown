package com.example.stockmarketshowdown.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LeaderboardViewModel : ViewModel() {

    private val leaderboardData = LeaderboardData.leaderboard.sortedByDescending { it.score }

    private var leaderboardList = MutableLiveData<List<LeaderboardEntry>>().apply {
        value = LeaderboardData.leaderboard.sortedByDescending { it.score }
    }

    fun getPosition(position: Int) : LeaderboardEntry {
        val note = leaderboardData[position]
        return note
    }

    fun fetchLeaderboardData(resultListener: () -> Unit) {
//        Fetch from DB in this case.
        leaderboardList.postValue(leaderboardData)
        resultListener.invoke()
    }

    fun observeLeaderboardList(): LiveData<List<LeaderboardEntry>> {
        return leaderboardList
    }
}
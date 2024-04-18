package com.example.stockmarketshowdown.model

data class LeaderboardEntry (
    val scoreID: Int,
    val userID: String,
    val score: Int,
    val displayName: String,
    val tagline: String
)
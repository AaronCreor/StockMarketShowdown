package com.example.stockmarketshowdown.model

data class UserProfile(
    val userID: String,
    val displayName: String?,
    val email: String?,
    val biography: String?,
    val tagline: String?,
    val cash: Double?,
    val picture: String?
)
package com.example.stockmarketshowdown.model

import java.sql.Date

data class TransactionHistory(
    var userID: String,
    var transactionId: String,
    var tradeType: String,
    var tradeValue: Double,
    var tradeDate: Date,
    var tradeCompany: String
)
package com.example.stockmarketshowdown.ui.history

import android.text.SpannableString
import java.sql.Date


data class TransactionHistory(
    var id: String,
    var TradeType: SpannableString,
    var TradeValue: Double,
    var TradeDate: Date,
    var TradeCompany: SpannableString)
object Transactions {
    val history = listOf(
        TransactionHistory(
            "1",
            SpannableString("Buy"),
            10000.00,
            Date.valueOf("2020-12-12"),
            SpannableString("Company A")
        ),
        TransactionHistory(
            "2",
            SpannableString("Sell"),
            500.50,
            Date.valueOf("2020-12-13"),
            SpannableString("Company A")
        ),
        TransactionHistory(
            "3",
            SpannableString("Buy"),
            10000.00,
            Date.valueOf("2020-12-14"),
            SpannableString("Company B")
        ),
        TransactionHistory(
            "4",
            SpannableString("Buy"),
            100.00,
            Date.valueOf("2020-12-15"),
            SpannableString("Company B")
        ),
        TransactionHistory(
            "5",
            SpannableString("Sell"),
            1.00,
            Date.valueOf("2020-12-12"),
            SpannableString("Company B")
        ),
        TransactionHistory(
            "6",
            SpannableString("Buy"),
            100000000000.00,
            Date.valueOf("2020-12-12"),
            SpannableString("Company C")
        ),
    )
}
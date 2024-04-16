package com.example.stockmarketshowdown.model

import java.math.BigDecimal

data class PortfolioEntry(
    val company: String,
    val totalOwnership: Int,
    val averagePrice: BigDecimal
)
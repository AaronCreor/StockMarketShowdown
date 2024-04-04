package com.example.stockmarketshowdown.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    @GET("api/v1/stock/profile2")
    fun getStockProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<StockProfileResponse>

    @GET("api/v1/quote")
    fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<QuoteResponse>

    companion object {
        private const val BASE_URL = "https://finnhub.io/"

        fun create(): FinnhubApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(FinnhubApi::class.java)
        }
    }


}

data class StockProfileResponse(
    val country: String,
    val currency: String,
    val estimateCurrency: String,
    val exchange: String,
    val finnhubIndustry: String,
    val ipo: String,
    val logo: String,
    val marketCapitalization: Double,
    val name: String,
    val phone: String,
    val shareOutstanding: Double,
    val ticker: String,
    val weburl: String
)

data class QuoteResponse(
    val c: Double, // current price
    val d: Double, // price change
    val dp: Double, // percentage change
    val h: Double, // high price
    val l: Double, // low price
    val o: Double, // open price
    val pc: Double, // previous close price
    val t: Long // timestamp
)
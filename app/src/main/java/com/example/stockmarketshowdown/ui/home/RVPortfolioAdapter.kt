package com.example.stockmarketshowdown.ui.home

import Repository
import android.content.Context
import java.math.BigDecimal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.api.FinnhubApi
import com.example.stockmarketshowdown.api.QuoteResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class Asset(
    val name: String,
    val quantity: Int,
    val value: BigDecimal,
    var totalValue: BigDecimal
)


class RVPortfolioAdapter(private val context: Context, private val assetList: MutableList<Asset>, private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<RVPortfolioAdapter.AssetViewHolder>() {

    private var sortColumn: SortColumn? = null
    private var ascending: Boolean = true

    fun updateAssets(newAssets: MutableList<Asset>) {
        assetList.clear()
        assetList.addAll(newAssets)
        notifyDataSetChanged()
    }



    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_asset_name)
        private val quantityTextView: TextView = itemView.findViewById(R.id.text_asset_quantity)
        private val valueTextView: TextView = itemView.findViewById(R.id.text_asset_value)
        private val totalValueTextView: TextView = itemView.findViewById(R.id.text_asset_total_val)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val asset = assetList[position]
                    val index = findCompanyIndexByTicker(asset.name)
                    listener(index)
                }
            }
        }

        fun bind(asset: Asset) {
            nameTextView.text = asset.name
            quantityTextView.text = asset.quantity.toString()
            valueTextView.text = (asset.value * asset.quantity.toBigDecimal()).toString()
            totalValueTextView.text = asset.totalValue.setScale(2, BigDecimal.ROUND_HALF_UP).toString()        }
    }

    private fun findCompanyIndexByTicker(ticker: String): Int {
        val repository = Repository()
        val companies = repository.loadCompaniesFromAssets(context)
        val companyIndex = companies.indexOfFirst { it.symbol == ticker }
        return if (companyIndex != -1) companyIndex else 0 // If not found, default to the first company
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.portfolio_row, parent, false)
        return AssetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assetList[position]
        holder.bind(asset)
    }

    fun fetchAssetTotalValue(asset: Asset, position: Int) {
        val apiService = FinnhubApi.create()
        val call = apiService.getQuote(asset.name, "co3lv89r01qj6vn86qd0co3lv89r01qj6vn86qdg")

        call.enqueue(object : Callback<QuoteResponse> {
            override fun onResponse(call: Call<QuoteResponse>, response: Response<QuoteResponse>) {
                if (response.isSuccessful) {
                    val quoteResponse = response.body()
                    if (quoteResponse != null) {
                        val currentPrice = quoteResponse.c.toBigDecimal()

                        // Update totalValue on the UI thread
                        asset.totalValue = currentPrice * BigDecimal(asset.quantity)
                        // Notify the adapter about the specific item that has been updated
                        notifyItemChanged(position)
                    }
                } else {
                    //TODO Handle API call error
                }
            }

            override fun onFailure(call: Call<QuoteResponse>, t: Throwable) {
                //TODO Handle API call failure
            }
        })
    }


    override fun getItemCount(): Int {
        return assetList.size
    }

    fun sortByColumn(column: SortColumn, ascending: Boolean) {
        this.sortColumn = column
        this.ascending = ascending

        when (column) {
            SortColumn.NAME -> {
                if (ascending) {
                    assetList.sortBy { it.name }
                } else {
                    assetList.sortByDescending { it.name }
                }
            }
            SortColumn.PRICE -> {
                if (ascending) {
                    assetList.sortBy { it.value }
                } else {
                    assetList.sortByDescending { it.value }
                }
            }
            SortColumn.QUANTITY -> {
                if (ascending) {
                    assetList.sortBy { it.quantity }
                } else {
                    assetList.sortByDescending { it.quantity }
                }
            }
            SortColumn.QUANTITY -> {
                if (ascending) {
                    assetList.sortBy { it.quantity }
                } else {
                    assetList.sortByDescending { it.quantity }
                }
            }
            SortColumn.TOTALVALUE -> {
                if (ascending) {
                    assetList.sortBy { it.totalValue }
                } else {
                    assetList.sortByDescending { it.totalValue }
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getSortColumn(): SortColumn? {
        return sortColumn
    }

    fun isAscending(): Boolean {
        return ascending
    }
}
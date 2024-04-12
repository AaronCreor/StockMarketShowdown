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


data class Asset(
    val name: String,
    val quantity: Int,
    val value: BigDecimal
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
            valueTextView.text = asset.value.toString()
        }
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
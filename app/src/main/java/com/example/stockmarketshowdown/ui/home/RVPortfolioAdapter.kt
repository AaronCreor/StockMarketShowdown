package com.example.stockmarketshowdown.ui.home

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
class RVPortfolioAdapter(private val assetList: MutableList<Asset>) :
    RecyclerView.Adapter<RVPortfolioAdapter.AssetViewHolder>() {

    fun updateAssets(newAssets: MutableList<Asset>) {
        assetList.clear()
        assetList.addAll(newAssets)
        notifyDataSetChanged()
    }

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_asset_name)
        private val quantityTextView: TextView = itemView.findViewById(R.id.text_asset_quantity)
        private val valueTextView: TextView = itemView.findViewById(R.id.text_asset_value)

        fun bind(asset: Asset) {
            nameTextView.text = asset.name
            quantityTextView.text = asset.quantity.toString()
            valueTextView.text = asset.value.toString()
        }

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
}
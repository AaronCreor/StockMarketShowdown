package com.example.stockmarketshowdown.ui.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.databinding.LeaderboardRowBinding
import com.example.stockmarketshowdown.model.LeaderboardEntry

class LeaderboardAdapter(private val viewModel: LeaderboardViewModel)
    : ListAdapter<LeaderboardEntry, LeaderboardAdapter.VH>(Diff()){

        class Diff : DiffUtil.ItemCallback<LeaderboardEntry>() {
            override fun areItemsTheSame(
                oldItem: LeaderboardEntry,
                newItem: LeaderboardEntry
            ): Boolean {
                return oldItem.scoreID == newItem.scoreID
            }

            override fun areContentsTheSame(
                oldItem: LeaderboardEntry,
                newItem: LeaderboardEntry
            ): Boolean {
                return oldItem.scoreID == newItem.scoreID
                        && oldItem.displayName == newItem.displayName
                        && oldItem.score == newItem.score
                        && oldItem.tagline == newItem.tagline
            }
        }

    inner class VH(private val rowBinding: LeaderboardRowBinding) :
            RecyclerView.ViewHolder(rowBinding.root) {
            fun bind(holder: VH, position: Int) {
                val leaderboardEntry = viewModel.getPosition(position)

                if (position == 0 || position == 1 || position == 2) {
                    holder.rowBinding.rowImage.visibility = VISIBLE
                    holder.rowBinding.rowPosition.visibility = GONE

                    if (position == 0) {
                        holder.rowBinding.root.setBackgroundColor(Color.rgb(240, 200, 91))
                        holder.rowBinding.rowImage.setImageResource(R.drawable.ic_gold_crown)
                    } else if (position == 1) {
                        holder.rowBinding.root.setBackgroundColor(Color.rgb(196, 193, 183))
                        holder.rowBinding.rowImage.setImageResource(R.drawable.ic_silver_crown)
                    } else {
                        holder.rowBinding.root.setBackgroundColor(Color.rgb(252, 167, 30))
                        holder.rowBinding.rowImage.setImageResource(R.drawable.ic_bronze_crown)
                    }
                } else {
                    holder.rowBinding.rowImage.visibility = GONE
                    holder.rowBinding.rowPosition.visibility = VISIBLE
                }

                holder.rowBinding.rowName.text = leaderboardEntry.displayName
//                val formatted = String.format("$%.2f", leaderboardEntry.score)
                holder.rowBinding.rowScore.text = leaderboardEntry.score.toString()
                holder.rowBinding.rowTagline.text = leaderboardEntry.tagline
                holder.rowBinding.rowPosition.text = (position + 1).toString()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = LeaderboardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }

}
package com.example.stockmarketshowdown.ui.leaderboard

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.databinding.LeaderboardRowBinding

class LeaderboardAdapter(private val viewModel: LeaderboardViewModel)
    : ListAdapter<LeaderboardEntry, LeaderboardAdapter.VH>(Diff()){

        class Diff : DiffUtil.ItemCallback<LeaderboardEntry>() {
            override fun areItemsTheSame(
                oldItem: LeaderboardEntry,
                newItem: LeaderboardEntry
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: LeaderboardEntry,
                newItem: LeaderboardEntry
            ): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.name == newItem.name
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
                } else {
                    holder.rowBinding.rowImage.visibility = GONE
                    holder.rowBinding.rowPosition.visibility = VISIBLE
                }

                holder.rowBinding.rowName.text = leaderboardEntry.name
                holder.rowBinding.rowScore.text = leaderboardEntry.score.toString()
                holder.rowBinding.rowTagline.text = leaderboardEntry.tagline
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
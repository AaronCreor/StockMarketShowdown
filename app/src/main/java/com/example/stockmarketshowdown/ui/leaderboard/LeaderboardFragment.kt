package com.example.stockmarketshowdown.ui.leaderboard

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.MainActivity
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.databinding.FragmentLeaderboardBinding
import com.example.stockmarketshowdown.model.LeaderboardEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLeaderboardBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")
        val mainActivity = (requireActivity() as MainActivity)
        val currentTime = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)
        val adapter = LeaderboardAdapter(viewModel)

        val rv = binding.recyclerView
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        binding.header.text = currentTime.format(formatter) + " Balance"

        viewModel.observeLeaderboardList().observe(viewLifecycleOwner) {
            val currentId = Firebase.auth.currentUser?.uid
            for ((index, entry) in it.withIndex()) {
                if (entry.userID == currentId) {
                    loadPersonalRank(entry, index + 1)
                }
            }
            adapter.submitList(it)
        }

        lifecycleScope.launch {
            mainActivity.progressBarOn()
            viewModel.fetchLeaderboardData {
                mainActivity.progressBarOff()
            }
        }
    }

    private fun loadPersonalRank(entry: LeaderboardEntry, position: Int) {
        binding.rowImage.visibility = View.GONE
        binding.rowName.text = entry.displayName
        binding.rowScore.text = entry.score.toString()
        binding.rowTagline.text = entry.tagline
        binding.rowPosition.text = position.toString()
    }
}
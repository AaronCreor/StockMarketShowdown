package com.example.stockmarketshowdown.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var portfolioAdapter: RVPortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userID = FirebaseAuth.getInstance().currentUser?.uid
        portfolioAdapter = RVPortfolioAdapter(mutableListOf())
        binding.recyclerAssets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }

        GlobalScope.launch(Dispatchers.Main) {
            // Get user's assets
            val assets = userID?.let { SMS().getUserAssets(it).toMutableList() }
            if (assets != null) {
                // Update assets in the RecyclerView
                portfolioAdapter.updateAssets(assets)

                // Calculate total asset value
                val totalAssetValue = assets.sumOf { it.value }.toString()

                // Get user's cash balance
                val cash = userID.let { SMS().getUserCash(it) }

                // Calculate total portfolio value
                val totalPortfolioValue = cash?.add(totalAssetValue.toBigDecimal())

                // Set the text of the TextView to display the total portfolio value with a "$" sign at the beginning
                binding.textPortfolioValue.text = "$$totalPortfolioValue"
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
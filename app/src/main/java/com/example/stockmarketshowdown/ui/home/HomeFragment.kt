package com.example.stockmarketshowdown.ui.home

import android.graphics.Color
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
import androidx.navigation.fragment.findNavController
import com.example.stockmarketshowdown.ui.home.HomeFragmentDirections

enum class SortColumn {
    NAME,
    PRICE,
    QUANTITY
}

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
        portfolioAdapter = RVPortfolioAdapter(requireContext(), mutableListOf()) { index ->
            val action = HomeFragmentDirections.actionNavigationHomeToCompanyPageFragment(index)
            findNavController().navigate(action)
        }
        binding.recyclerAssets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }

        GlobalScope.launch(Dispatchers.Main) {
            val assets = userID?.let { SMS().getUserAssets(it).toMutableList() }
            if (assets != null) {
                portfolioAdapter.updateAssets(assets)
                setupSorting()
                val totalAssetValue = assets.sumOf { it.value }.toString()
                val cash = userID.let { SMS().getUserCash(it) }
                val totalPortfolioValue = cash?.add(totalAssetValue.toBigDecimal())
                binding.textPortfolioValue.text = "$$totalPortfolioValue"
            }
        }

        return root
    }

    private fun setupSorting() {
        binding.textTickerTitle.setOnClickListener {
            if (::portfolioAdapter.isInitialized) {
                portfolioAdapter.sortByColumn(SortColumn.NAME, !portfolioAdapter.isAscending())
                updateSortTitleBackground(binding.textTickerTitle, SortColumn.NAME)
            }
        }

        binding.textValueTitle.setOnClickListener {
            if (::portfolioAdapter.isInitialized) {
                portfolioAdapter.sortByColumn(SortColumn.PRICE, !portfolioAdapter.isAscending())
                updateSortTitleBackground(binding.textValueTitle, SortColumn.PRICE)
            }
        }

        binding.textQuantityTitle.setOnClickListener {
            if (::portfolioAdapter.isInitialized) {
                portfolioAdapter.sortByColumn(SortColumn.QUANTITY, !portfolioAdapter.isAscending())
                updateSortTitleBackground(binding.textQuantityTitle, SortColumn.QUANTITY)
            }
        }
    }

    private fun updateSortTitleBackground(view: View, column: SortColumn) {
        // Reset all column title colors to transparent
        binding.textTickerTitle.setBackgroundColor(Color.TRANSPARENT)
        binding.textValueTitle.setBackgroundColor(Color.TRANSPARENT)
        binding.textQuantityTitle.setBackgroundColor(Color.TRANSPARENT)

        // Set the background color of the clicked title to yellow
        val backgroundColor = if (portfolioAdapter.getSortColumn() == column) {
            if (portfolioAdapter.isAscending()) Color.YELLOW else Color.RED
        } else {
            Color.TRANSPARENT
        }
        view.setBackgroundColor(backgroundColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.stockmarketshowdown.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.ui.home.HomeFragmentDirections
import kotlinx.coroutines.Job
import java.math.BigDecimal

enum class SortColumn {
    NAME,
    PRICE,
    QUANTITY,
    TOTALVALUE
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null
    private lateinit var portfolioAdapter: RVPortfolioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.imageTrendIcon.visibility = View.INVISIBLE
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        portfolioAdapter = RVPortfolioAdapter(requireContext(), mutableListOf()) { index ->
            val action = HomeFragmentDirections.actionNavigationHomeToCompanyPageFragment(index)
            findNavController().navigate(action)
        }
        binding.recyclerAssets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = portfolioAdapter
        }

        job = GlobalScope.launch(Dispatchers.Main) {
            val assets = userID?.let { SMS().getUserAssets(it).toMutableList() }
            if (assets != null) {
                assets.forEachIndexed { index, asset ->
                    portfolioAdapter.fetchAssetTotalValue(asset, index)
                }
                setupSorting()

                val cash = userID.let { SMS().getUserCash(it) }
                portfolioAdapter.updateAssets(assets)
                val totalAssetValue = assets.sumOf { it.totalValue }.toString()
                val totalPortfolioValue = cash?.add(totalAssetValue.toBigDecimal())
                binding.textPortfolioValue.text = "$$totalPortfolioValue"

                if (totalPortfolioValue != null) {

                    updateScore(userID, totalPortfolioValue)
                }
            }
        }
        binding.imageGlobe.setOnClickListener {
            // Navigate to the GlobalViewFragment
            findNavController().navigate(HomeFragmentDirections.actionHomeToGlobalView())
        }

        return root
    }

    private suspend fun updateScore(userID: String?, totalPortfolioValue: BigDecimal) {
        userID?.let {
            val currentScore = SMS().getScore(it)
            val newScore = totalPortfolioValue.toInt()

            if (currentScore == null) {
                SMS().setScore(it, newScore)
                // No need to set trend indicator or text color here
            } else if (newScore > currentScore) {
                SMS().updateScore(it, newScore)
                binding.imageTrendIcon.visibility = View.VISIBLE
                binding.imageTrendIcon.setImageResource(R.drawable.ic_up_triangle)
                binding.textPortfolioValue.setTextColor(Color.GREEN) // Set text color to green
            } else if (newScore < currentScore) {
                SMS().updateScore(it, newScore)
                binding.imageTrendIcon.visibility = View.VISIBLE
                binding.imageTrendIcon.setImageResource(R.drawable.ic_down_triangle)
                binding.textPortfolioValue.setTextColor(Color.RED) // Set text color to red
            } else {
                binding.imageTrendIcon.visibility = View.INVISIBLE
            }
        }
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

        binding.textTotalValueTitle.setOnClickListener {
            if (::portfolioAdapter.isInitialized) {
                portfolioAdapter.sortByColumn(SortColumn.TOTALVALUE, !portfolioAdapter.isAscending())
                updateSortTitleBackground(binding.textTotalValueTitle, SortColumn.TOTALVALUE)
            }
        }

    }

    private fun updateSortTitleBackground(view: View, column: SortColumn) {
        // Reset all column title to normal
        binding.textTickerTitle.setTypeface(null, Typeface.NORMAL)
        binding.textValueTitle.setTypeface(null, Typeface.NORMAL)
        binding.textQuantityTitle.setTypeface(null, Typeface.NORMAL)
        binding.textTotalValueTitle.setTypeface(null, Typeface.NORMAL)

        if (column == SortColumn.NAME) {
            if (portfolioAdapter.isAscending()) {
                binding.textTickerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                binding.textTickerTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
            binding.textTickerTitle.setTypeface(binding.textTickerTitle.typeface, Typeface.BOLD)
        } else if (column == SortColumn.PRICE) {
            if (portfolioAdapter.isAscending()) {
                binding.textValueTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                binding.textValueTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
            binding.textValueTitle.setTypeface(binding.textValueTitle.typeface, Typeface.BOLD)
        } else if (column == SortColumn.QUANTITY) {
            if (portfolioAdapter.isAscending()) {
                binding.textQuantityTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                binding.textQuantityTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
            binding.textQuantityTitle.setTypeface(binding.textQuantityTitle.typeface, Typeface.BOLD)
        } else {
            if (portfolioAdapter.isAscending()) {
                binding.textTotalValueTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                binding.textTotalValueTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
            binding.textTotalValueTitle.setTypeface(binding.textTotalValueTitle.typeface, Typeface.BOLD)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        job?.cancel()//this stops us from crashing when we swap views too fast
    }
}
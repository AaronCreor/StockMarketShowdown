package com.example.stockmarketshowdown.ui.history

import android.graphics.Typeface
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
import com.example.stockmarketshowdown.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHistoryBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")

        val adapter = HistoryAdapter(viewModel)
        val mainActivity = (requireActivity() as MainActivity)

        val rv = binding.recyclerView
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)

        Log.d(javaClass.simpleName, "submitList")

        binding.headerCompany.setOnClickListener {
            mainActivity.progressBarOn()
            lifecycleScope.launch {
                viewModel.sortInfoClick(SortColumn.COMPANY) {
                    mainActivity.progressBarOff()
                }
            }
        }

        binding.headerDate.setOnClickListener {
            mainActivity.progressBarOn()
            lifecycleScope.launch {
                viewModel.sortInfoClick(SortColumn.DATE) {
                    mainActivity.progressBarOff()
                }
            }
        }

        binding.headerType.setOnClickListener {
            mainActivity.progressBarOn()
            lifecycleScope.launch {
                viewModel.sortInfoClick(SortColumn.TYPE) {
                    mainActivity.progressBarOff()
                }
            }
        }

        binding.headerValue.setOnClickListener {
            mainActivity.progressBarOn()
            lifecycleScope.launch {
                viewModel.sortInfoClick(SortColumn.VALUE) {
                    mainActivity.progressBarOff()
                }
            }
        }

        viewModel.observeSortInfo().observe(viewLifecycleOwner) {
            binding.headerType.setTypeface(null, Typeface.NORMAL)
            binding.headerCompany.setTypeface(null, Typeface.NORMAL)
            binding.headerDate.setTypeface(null, Typeface.NORMAL)
            binding.headerValue.setTypeface(null, Typeface.NORMAL)
            if (it.sortColumn == SortColumn.COMPANY) {
                if (it.ascending) {
                    binding.headerCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                } else {
                    binding.headerCompany.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                }
                binding.headerCompany.setTypeface(binding.headerCompany.typeface, Typeface.BOLD)
            } else if (it.sortColumn == SortColumn.DATE) {
                if (it.ascending) {
                    binding.headerDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                } else {
                    binding.headerDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                }
                binding.headerDate.setTypeface(binding.headerDate.typeface, Typeface.BOLD)
            } else if (it.sortColumn == SortColumn.TYPE) {
                if (it.ascending) {
                    binding.headerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                } else {
                    binding.headerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                }
                binding.headerType.setTypeface(binding.headerType.typeface, Typeface.BOLD)
            } else if (it.sortColumn == SortColumn.VALUE) {
                if (it.ascending) {
                    binding.headerValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                } else {
                    binding.headerValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                }
                binding.headerValue.setTypeface(binding.headerValue.typeface, Typeface.BOLD)
            }
        }

        viewModel.observeHistory().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        lifecycleScope.launch {
            mainActivity.progressBarOn()
            viewModel.fetchTransactionHistory {
                mainActivity.progressBarOff()
            }
        }
    }


}
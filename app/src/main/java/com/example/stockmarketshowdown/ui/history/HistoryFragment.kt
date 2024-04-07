package com.example.stockmarketshowdown.ui.history

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null

    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHistoryBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")

        val adapter = HistoryAdapter(viewModel)

        val rv = binding.recyclerView
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        adapter.submitList(viewModel.getTransactions())
        Log.d(javaClass.simpleName, "submitList")

        binding.headerCompany.setOnClickListener {
            viewModel.sortInfoClick(SortColumn.COMPANY) {

            }
        }

        binding.headerDate.setOnClickListener {
            viewModel.sortInfoClick(SortColumn.DATE) {

            }
        }

        binding.headerType.setOnClickListener {
            viewModel.sortInfoClick(SortColumn.TYPE) {

            }
        }

        binding.headerValue.setOnClickListener {
            viewModel.sortInfoClick(SortColumn.VALUE) {

            }
        }

        viewModel.observeSortInfo().observe(viewLifecycleOwner) {
            if (it.sortColumn == SortColumn.COMPANY) {
                binding.headerType.setBackgroundColor(Color.TRANSPARENT)
                binding.headerDate.setBackgroundColor(Color.TRANSPARENT)
                binding.headerValue.setBackgroundColor(Color.TRANSPARENT)
                if (it.ascending) {
                    binding.headerCompany.setBackgroundColor(Color.YELLOW)
                } else {
                    binding.headerCompany.setBackgroundColor(Color.RED)
                }
            } else if (it.sortColumn == SortColumn.DATE) {
                binding.headerType.setBackgroundColor(Color.TRANSPARENT)
                binding.headerCompany.setBackgroundColor(Color.TRANSPARENT)
                binding.headerValue.setBackgroundColor(Color.TRANSPARENT)
                if (it.ascending) {
                    binding.headerDate.setBackgroundColor(Color.YELLOW)
                } else {
                    binding.headerDate.setBackgroundColor(Color.RED)
                }
            } else if (it.sortColumn == SortColumn.TYPE) {
                binding.headerCompany.setBackgroundColor(Color.TRANSPARENT)
                binding.headerDate.setBackgroundColor(Color.TRANSPARENT)
                binding.headerValue.setBackgroundColor(Color.TRANSPARENT)
                if (it.ascending) {
                    binding.headerType.setBackgroundColor(Color.YELLOW)
                } else {
                    binding.headerType.setBackgroundColor(Color.RED)
                }
            } else if (it.sortColumn == SortColumn.VALUE) {
                binding.headerType.setBackgroundColor(Color.TRANSPARENT)
                binding.headerDate.setBackgroundColor(Color.TRANSPARENT)
                binding.headerCompany.setBackgroundColor(Color.TRANSPARENT)
                if (it.ascending) {
                    binding.headerValue.setBackgroundColor(Color.YELLOW)
                } else {
                    binding.headerValue.setBackgroundColor(Color.RED)
                }
            }
        }

        viewModel.observeHistoryList().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


    }


}
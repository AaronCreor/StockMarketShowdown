package com.example.stockmarketshowdown.ui.dashboard

import RVDiffAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.FragmentDashboardBinding
import androidx.navigation.fragment.findNavController

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RVDiffAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        adapter = RVDiffAdapter(viewModel) { company ->
            findNavController().navigate(
                DashboardFragmentDirections.actionNavigationDashboardToCompanyPageFragment(viewModel.getCurrentCompanyInfoByTicker(company.displaySymbol))
            )
        }
        binding.companyRV.layoutManager = LinearLayoutManager(requireContext())
        binding.companyRV.adapter = adapter

        viewModel.liveCompanyList.observe(viewLifecycleOwner, Observer { companies ->
            adapter.submitList(companies)
            adapter.setCompanyListFull(companies)
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filter(newText)
                }
                return true
            }
        })

        adapter.setOnItemClickListener { company ->
            findNavController().navigate(
                DashboardFragmentDirections.actionNavigationDashboardToCompanyPageFragment(viewModel.getCurrentCompanyInfoByTicker(company.displaySymbol))
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.setQuery("", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
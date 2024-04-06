package com.example.stockmarketshowdown.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.FragmentDashboardBinding
import musicplayer.cs371m.musicplayer.RVDiffAdapter
import androidx.navigation.fragment.findNavController
import com.example.stockmarketshowdown.Database.SMS

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
            // Handle item click here, navigate to CompanyPageFragment
            findNavController().navigate(DashboardFragmentDirections.actionNavigationDashboardToCompanyPageFragment(company))
        }

        binding.companyRV.layoutManager = LinearLayoutManager(requireContext())
        binding.companyRV.adapter = adapter

        // Observe changes in companyList
        viewModel.liveCompanyList.observe(viewLifecycleOwner, Observer { companies ->
            // Update the RecyclerView adapter with new data
            adapter.submitList(companies)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
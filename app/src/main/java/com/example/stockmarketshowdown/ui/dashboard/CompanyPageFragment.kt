package com.example.stockmarketshowdown.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.FragmentCompanypageBinding

class CompanyPageFragment : Fragment() {

    private var _binding: FragmentCompanypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val index = arguments?.getInt("companyIndex") ?: -1
        if (index != -1) {
            val companyDescription = viewModel.getCurrentCompanyInfo(index)
                binding.textHome.text = companyDescription
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
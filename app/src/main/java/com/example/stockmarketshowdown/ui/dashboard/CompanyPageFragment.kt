package com.example.stockmarketshowdown.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stockmarketshowdown.databinding.FragmentCompanypageBinding

class CompanyPageFragment : Fragment() {

    private var _binding: FragmentCompanypageBinding? = null
    private val binding get() = _binding!!

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

        // Initialize UI components and set listeners here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
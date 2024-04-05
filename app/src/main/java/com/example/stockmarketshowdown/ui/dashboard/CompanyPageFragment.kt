package com.example.stockmarketshowdown.ui.dashboard

import Company
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.FragmentCompanypageBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.api.FinnhubApi
import com.example.stockmarketshowdown.api.QuoteResponse
import com.example.stockmarketshowdown.api.StockProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CompanyPageFragment : Fragment() {

    private var _binding: FragmentCompanypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var finnhubApi: FinnhubApi // Add this variable to hold the FinnhubApi instance
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
        finnhubApi = FinnhubApi.create()
        val index = arguments?.getInt("companyIndex") ?: -1
        val company = viewModel.getCurrentCompanyInfo(index)
        apiStuff(company)


        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call popBackStack when back button is pressed
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun apiStuff(company: Company){
        finnhubApi.getStockProfile(company.displaySymbol, "co3lv89r01qj6vn86qd0co3lv89r01qj6vn86qdg").enqueue(object : Callback<StockProfileResponse> {
            override fun onResponse(call: Call<StockProfileResponse>, response: Response<StockProfileResponse>) {
                if (response.isSuccessful) {
                    val stockProfileResponse = response.body()
                    val ipoDate = stockProfileResponse?.ipo
                    binding.ipo.text = ipoDate
                } else {
                    //TODO unsuccessful response
                }
            }
            override fun onFailure(call: Call<StockProfileResponse>, t: Throwable) {
                //TODO Handle failure
            }
        })

        // Fetch current price
        finnhubApi.getQuote(company.displaySymbol, "co3lv89r01qj6vn86qd0co3lv89r01qj6vn86qdg").enqueue(object : Callback<QuoteResponse> {
            override fun onResponse(call: Call<QuoteResponse>, response: Response<QuoteResponse>) {
                if (response.isSuccessful) {
                    val quoteResponse = response.body()
                    val currentPrice = quoteResponse?.c
                    binding.price.text = currentPrice.toString()
                } else {
                    //TODO unsuccessful response
                }
            }
            override fun onFailure(call: Call<QuoteResponse>, t: Throwable) {
                //TODO Handle failure
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}

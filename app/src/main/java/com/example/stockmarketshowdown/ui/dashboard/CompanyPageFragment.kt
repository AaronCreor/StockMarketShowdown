package com.example.stockmarketshowdown.ui.dashboard

import Company
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.api.FinnhubApi
import com.example.stockmarketshowdown.api.QuoteResponse
import com.example.stockmarketshowdown.api.StockProfileResponse
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.databinding.FragmentCompanypageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
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
    private fun showSnackbar(message: String) {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams
        snackbar.view.layoutParams = params
        snackbar.show()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        finnhubApi = FinnhubApi.create()
        fetchUserPortfolio()
        fetchUserCash()
        val spinner = binding.progressBar
        binding.buyButton.setOnClickListener {
            // Get the quantity and price
            val quantity = binding.quantityEditText.text.toString().toIntOrNull()
            val price = binding.price.text.toString().toDoubleOrNull()

            if (quantity != null && quantity > 0 && price != null) {
                val totalCost = quantity * price
                val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

                lifecycleScope.launch {
                    val userCash = SMS().getUserCash(currentUserUID!!)
                    if (userCash != null) {
                        if (userCash >= totalCost.toBigDecimal()) {
                            showConfirmationDialogueBuy(totalCost)
                        } else {
                            showSnackbar("Insufficient funds")
                        }
                    }
                }
            } else {
                showSnackbar("Please enter a valid quantity")
            }
        }
            spinner.visibility = View.GONE
        binding.sellButton.setOnClickListener {
            // Get the quantity and price
            val quantity = binding.quantityEditText.text.toString().toIntOrNull()
            val price = binding.price.text.toString().toDoubleOrNull()

            if (quantity != null && quantity > 0 && price != null) {
                val totalCost = quantity * price
                val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

                lifecycleScope.launch {
                    val userCash = SMS().getUserCash(currentUserUID!!)
                    if (userCash != null) {
                        val userPortfolio = SMS().getUserPortfolio(currentUserUID)
                        val stockToSell =
                            userPortfolio.firstOrNull { it.company == binding.ticker.text.toString() }
                        if (stockToSell != null && stockToSell.totalOwnership >= quantity) {
                            showConfirmationDialogueSell(totalCost)
                        } else {
                            showSnackbar("Insufficient stocks to sell")
                        }
                    }
                }
            } else {
                showSnackbar("Please enter a valid quantity")
            }
        }
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

    private fun showConfirmationDialogueBuy(totalCost: Double) {
        val builder = AlertDialog.Builder(requireContext())
        val costText = totalCost.formatToTwoDecimalPlaces().toString()
        builder.setTitle("Confirm Trade")
        builder.setMessage("Are you sure you want to execute this trade?\nTotal value: $costText")
        builder.setPositiveButton("Yes") { dialog, _ ->
            executeBuy()
            dialog.dismiss()
            clearQuantityEditText()
            hideKeyboard()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            clearQuantityEditText()
            hideKeyboard()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun executeBuy() {
        val quantity = binding.quantityEditText.text.toString().toInt()
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
        val price = binding.price.text.toString().toDouble()
        val totalCost = quantity * price
        val costText = totalCost.formatToTwoDecimalPlaces().toString()

        lifecycleScope.launch {
            val remainingCash = SMS().getUserCash(currentUserUID!!)!! - totalCost.toBigDecimal()
            SMS().insertTransaction(currentUserUID, "BUY", totalCost.toBigDecimal(), binding.ticker.text.toString())
            SMS().updateCash(currentUserUID, remainingCash)
            SMS().insertPortfolio(currentUserUID, binding.ticker.text.toString(), quantity, totalCost.toBigDecimal())
            fetchUserPortfolio()
            fetchUserCash()
            showSnackbar("Buy executed successfully. Total value: $costText")
        }
    }

    private fun showConfirmationDialogueSell(totalCost: Double) {
        val builder = AlertDialog.Builder(requireContext())
        val costText = totalCost.formatToTwoDecimalPlaces().toString()
        builder.setTitle("Confirm Trade")
        builder.setMessage("Are you sure you want to execute this trade?\nTotal value: $costText")
        builder.setPositiveButton("Yes") { dialog, _ ->
            executeSell()
            dialog.dismiss()
            clearQuantityEditText()
            hideKeyboard()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            clearQuantityEditText()
            hideKeyboard()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun executeSell() {
        val quantity = binding.quantityEditText.text.toString().toInt()
        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
        val price = binding.price.text.toString().toDouble()
        val totalCost = quantity * price
        val costText = totalCost.formatToTwoDecimalPlaces().toString()

        lifecycleScope.launch {
            val remainingCash = SMS().getUserCash(currentUserUID!!)!! + totalCost.toBigDecimal()
            SMS().insertTransaction(currentUserUID, "SELL", totalCost.toBigDecimal(), binding.ticker.text.toString())
            SMS().updateCash(currentUserUID, remainingCash)
            SMS().updatePortfolio(currentUserUID, binding.ticker.text.toString(), -quantity)
            fetchUserPortfolio()
            fetchUserCash()
            showSnackbar("Sell executed successfully. Total value: $costText")
        }
    }

    private fun clearQuantityEditText() {
        binding.quantityEditText.text.clear()
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.quantityEditText.windowToken, 0)
    }

    private fun Double.formatToTwoDecimalPlaces(): Double {
        return String.format("%.2f", this).toDouble()
    }

    private fun apiStuff(company: Company){
        finnhubApi.getStockProfile(company.displaySymbol, "co3lv89r01qj6vn86qd0co3lv89r01qj6vn86qdg").enqueue(object : Callback<StockProfileResponse> {
            override fun onResponse(call: Call<StockProfileResponse>, response: Response<StockProfileResponse>) {
                if (response.isSuccessful) {
                    val stockProfileResponse = response.body()
                    binding.webUrl.text = "Website"
                    binding.companyName.text = stockProfileResponse?.name
                    binding.ticker.text = stockProfileResponse?.ticker
                    binding.exchange.text = stockProfileResponse?.exchange
                    binding.webUrl.setOnClickListener {
                        val url = stockProfileResponse?.weburl
                        if (!url.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } else {
                            //TODO Handle case where URL is empty or null
                        }
                    }
                    binding.industry.text = stockProfileResponse?.finnhubIndustry
                    binding.ipo.text = stockProfileResponse?.ipo
                    val logoUrl = stockProfileResponse?.logo
                    if (!logoUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(logoUrl)
                            .into(binding.companyLogo)
                    } else {
                        //TODO Handle case where logo URL is empty or null
                    }
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

    private fun fetchUserPortfolio() {
        lifecycleScope.launch {
            val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
            val userPortfolio = SMS().getUserPortfolio(currentUserUID!!)
            val companySymbol = binding.ticker.text.toString()
            val ownedShares = userPortfolio.firstOrNull { it.company == companySymbol }?.totalOwnership ?: 0
            binding.sharesOwned.text = ownedShares.toString()
        }
    }

    private fun fetchUserCash() {
        lifecycleScope.launch {
            val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
            val userCash = SMS().getUserCash(currentUserUID!!)
            val formattedCash = "$${userCash.toString()}"
            binding.cashOwned.text = formattedCash
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}

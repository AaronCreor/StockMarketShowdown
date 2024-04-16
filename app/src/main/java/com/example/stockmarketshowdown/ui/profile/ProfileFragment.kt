package com.example.stockmarketshowdown.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.stockmarketshowdown.MainActivity
import com.example.stockmarketshowdown.R
import com.example.stockmarketshowdown.databinding.FragmentProfileBinding
import com.example.stockmarketshowdown.model.UserProfile
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by activityViewModels()

    var userProfile = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")
        val mainActivity = (requireActivity() as MainActivity)

        viewModel.observeUserProfile().observe(viewLifecycleOwner) {
            loadProfile(it)
        }

        binding.editButton.setOnClickListener {
            turnOnEdit()
        }

        binding.exitButton.setOnClickListener {
            binding.editTagline.text.clear()
            binding.editBiography.text.clear()
            binding.editDisplayName.text.clear()
            turnOffEdit()
        }

        binding.saveButton.setOnClickListener {
            mainActivity.progressBarOn()
            val displayName = binding.editDisplayName.text.toString()
            val bio = binding.editBiography.text.toString()
            val tag = binding.editTagline.text.toString()
            lifecycleScope.launch {
                // update Profile
                viewModel.updateUserProfile(displayName, tag, bio) {
                    mainActivity.progressBarOff()
                }
            }
            turnOffEdit()
        }

        lifecycleScope.launch {
            mainActivity.progressBarOn()
            viewModel.fetchUserProfile {
                mainActivity.progressBarOff()
            }
        }
    }

    private fun loadProfile(userProfile: UserProfile) {
        binding.biography.text = userProfile.biography
        binding.displayName.text = userProfile.displayName
        binding.email.text = userProfile.email
        binding.netWorth.text = userProfile.cash.toString()
        binding.tagline.text = userProfile.tagline
        binding.username.text = userProfile.displayName
    }

    private fun turnOnEdit() {
        binding.displayName.visibility = View.GONE
        binding.editDisplayName.visibility = View.VISIBLE
        binding.editDisplayName.setText(viewModel.getDisplayName())

        binding.biography.visibility = View.GONE
        binding.editBiography.visibility = View.VISIBLE
        binding.editBiography.setText(viewModel.getAbout())

        binding.tagline.visibility = View.GONE
        binding.editTagline.visibility = View.VISIBLE
        binding.editTagline.setText(viewModel.getTagline())

        binding.editButton.visibility = View.GONE
        binding.saveButton.visibility = View.VISIBLE
        binding.exitButton.visibility = View.VISIBLE
    }

    private fun turnOffEdit() {
        binding.displayName.visibility = View.VISIBLE
        binding.editDisplayName.visibility = View.GONE

        binding.biography.visibility = View.VISIBLE
        binding.editBiography.visibility = View.GONE

        binding.tagline.visibility = View.VISIBLE
        binding.editTagline.visibility = View.GONE

        binding.editButton.visibility = View.VISIBLE
        binding.saveButton.visibility = View.GONE
        binding.exitButton.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
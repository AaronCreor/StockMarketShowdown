package com.example.stockmarketshowdown.firebase

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.stockmarketshowdown.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class AuthInit(viewModel: MainViewModel, signInLaucher: ActivityResultLauncher<Intent>) {

    companion object {
        private const val TAG = "AuthInit"
        fun setDisplayName(displayName: String, viewModel: MainViewModel) {
            Log.d(TAG, "XXX profile change request")
            val user = Firebase.auth.currentUser
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("AuthInit", "user Updates Profile.")
                        viewModel.updateUser()
                    }
                }
        }
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d(TAG, "xxx user null")

            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLaucher.launch(signInIntent)
        } else {
            Log.d(TAG, "XXX user ${user.displayName} email ${user.email}")
            viewModel.updateUser()
        }
    }
}
package com.example.stockmarketshowdown.firebase

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.Database.SMS
import com.firebase.ui.auth.AuthUI
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    fun getUser(): FirebaseUser? {
        val user = FirebaseAuth.getInstance().currentUser
        return user
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
            SMS().generateDBUser()
        }

        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is signed in
                Log.d(TAG, "User ${user.displayName} is signed in. Performing necessary actions.")
                viewModel.updateUser()
                SMS().generateDBUser()
            } else {
                // User is signed out
                Log.d(TAG, "No user signed in.")
            }
        }

    }
}
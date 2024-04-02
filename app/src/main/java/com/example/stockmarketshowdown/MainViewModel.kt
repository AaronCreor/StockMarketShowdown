package com.example.stockmarketshowdown

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainViewModel : ViewModel() {
    private var displayName = MutableLiveData("Uninitialized")
    private var email = MutableLiveData("Uninitialized")
    private var uid = MutableLiveData("Uninitialized")

    private fun userLogout() {
        displayName.postValue("No user")
        email.postValue("No email, no active user")
        uid.postValue("No uid, no active user")
    }

    fun updateUser() {
        val user = Firebase.auth.currentUser
    }

    fun observeDisplayName() : LiveData<String> {
        return displayName
    }

    fun observeEmail() : LiveData<String> {
        return email
    }

    fun observeUid() : LiveData<String> {
        return uid
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }
}
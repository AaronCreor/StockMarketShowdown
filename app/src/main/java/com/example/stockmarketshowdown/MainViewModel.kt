package com.example.stockmarketshowdown

import Company
import Repository
import android.content.Context
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
    val repository = Repository()
    private var _companyList = listOf<Company>()
    private val _liveCompanyList = MutableLiveData<List<Company>>()
    val liveCompanyList: LiveData<List<Company>>
        get() = _liveCompanyList

    private fun userLogout() {
        displayName.postValue("No user")
        email.postValue("No email, no active user")
        uid.postValue("No uid, no active user")
    }
    fun populateCompanyList(context: Context) {
        _companyList = repository.loadCompaniesFromAssets(context)
        _liveCompanyList.value = _companyList // Update LiveData with new list
    }

    fun getCurrentCompanyInfo(index: Int) : Company {
        return _companyList[index]
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
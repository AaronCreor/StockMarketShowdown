package com.example.stockmarketshowdown.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stockmarketshowdown.database.SMS
import com.example.stockmarketshowdown.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileViewModel : ViewModel() {

    private val sms = SMS()

    private var userProfile = MutableLiveData<UserProfile>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    suspend fun fetchUserProfile(resultListener: () -> Unit) {
        val userID = Firebase.auth.currentUser?.uid
        userProfile.postValue(sms.getUser(userID!!))
        resultListener.invoke()
    }

    suspend fun updateUserProfile(displayName: String, tagline: String, biography: String, resultListener: () -> Unit) {
        val data = UserProfile(
            userProfile.value!!.userID,
            displayName,
            userProfile.value!!.email,
            biography,
            tagline,
            userProfile.value!!.cash,
            userProfile.value!!.picture
        )
        sms.updateUserProfile(data)
        fetchUserProfile(resultListener)
    }

    suspend fun updateUserPicture(url: String, resultListener: () -> Unit) {
        val data = UserProfile(
            userProfile.value!!.userID,
            userProfile.value!!.displayName,
            userProfile.value!!.email,
            userProfile.value!!.biography,
            userProfile.value!!.tagline,
            userProfile.value!!.cash,
            url
        )
        sms.updateUserProfile(data)
        fetchUserProfile(resultListener)
    }

    fun observeUserProfile(): LiveData<UserProfile> {
        return userProfile
    }

    fun getDisplayName(): String? {
        return userProfile.value!!.displayName
    }

    fun getTagline(): String? {
        return userProfile.value!!.tagline
    }

    fun getAbout(): String? {
        return userProfile.value!!.biography
    }
}
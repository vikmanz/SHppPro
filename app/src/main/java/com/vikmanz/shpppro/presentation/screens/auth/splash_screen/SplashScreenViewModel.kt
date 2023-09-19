package com.vikmanz.shpppro.presentation.screens.auth.splash_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vikmanz.shpppro.common.Constants
import com.vikmanz.shpppro.data.datastore.Datastore
import com.vikmanz.shpppro.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    dataStore: Datastore
) : BaseViewModel() {

    val login = MutableLiveData("")

    /**
     * Check if user already save login-password, and do autologin if it's need.
     */
    init {
        viewModelScope.launch {
            delay(Constants.SPLASH_DELAY)
            dataStore.userEmail.collect { email ->
                if (email.isBlank()) startLoginFragment() else startMainActivity(email)
            }
        }
    }

    private fun startLoginFragment() {
        val direction = SplashScreenFragmentDirections.startSignInFragment()
        navigate(direction)
    }

    private fun startMainActivity(email: String) {
        val direction = SplashScreenFragmentDirections.startMainActivity(email)
        navigateToActivity(direction)
    }
}
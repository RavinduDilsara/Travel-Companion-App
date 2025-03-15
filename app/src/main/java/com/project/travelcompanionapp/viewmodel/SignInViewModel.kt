package com.project.travelcompanionapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.travelcompanionapp.repository.AuthRepository
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _signInResult = MutableLiveData<String>()
    val signInResult: LiveData<String> get() = _signInResult

    fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                val result = authRepository.signInWithEmailPassword(email, password)
                if (result.isSuccess) {
                    _signInResult.value = "success"
                } else {
                    _signInResult.value = result.exceptionOrNull()?.localizedMessage ?: "Authentication failed"
                }
            }
        } else {
            _signInResult.value = "Empty fields are not allowed !!"
        }
    }

    fun checkUserStatus(): Boolean {
        return authRepository.checkUserStatus()
    }
}

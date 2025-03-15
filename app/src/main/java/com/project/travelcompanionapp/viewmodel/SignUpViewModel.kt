package com.project.travelcompanionapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.travelcompanionapp.repository.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password.length < 6) {
                _signUpResult.value = "Password should be at least 6 characters long"
                return
            }

            if (password == confirmPassword) {
                viewModelScope.launch {
                    val result = authRepository.signUpWithEmailPassword(email, password)
                    if (result.isSuccess) {
                        _signUpResult.value = "success"
                    } else {
                        _signUpResult.value = result.exceptionOrNull()?.localizedMessage ?: "Authentication failed"
                    }
                }
            } else {
                _signUpResult.value = "Passwords do not match"
            }
        } else {
            _signUpResult.value = "Empty fields are not allowed"
        }
    }
}

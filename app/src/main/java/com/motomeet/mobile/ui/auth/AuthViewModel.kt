package com.motomeet.mobile.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motomeet.mobile.data.model.LoginRequest
import com.motomeet.mobile.data.model.RegisterRequest
import com.motomeet.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun login(email: String, password: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = AuthState.Error("Please enter a valid email")
            return
        }
        if (password.isBlank()) {
            _loginState.value = AuthState.Error("Password is required")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = repository.login(LoginRequest(email, password))
            result.onSuccess {
                _loginState.value = AuthState.Success(it)
            }.onFailure {
                _loginState.value = AuthState.Error(it.message ?: "Login failed")
            }
        }
    }

    fun register(firstname: String, lastname: String, email: String, password: String, confirmPass: String) {
        if (firstname.isBlank() || lastname.isBlank()) {
            _registerState.value = AuthState.Error("Name fields are required")
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = AuthState.Error("Invalid email format")
            return
        }
        if (password.length < 6) {
            _registerState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPass) {
            _registerState.value = AuthState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = repository.register(RegisterRequest(firstname, lastname, email, password))
            result.onSuccess {
                _registerState.value = AuthState.Success(it)
            }.onFailure {
                _registerState.value = AuthState.Error(it.message ?: "Registration failed")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }
}

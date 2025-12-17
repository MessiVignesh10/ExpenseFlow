package com.example.expenseflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenseflow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message : String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    init {
        if (authRepository.getCurrentUser() != null) {
            _authState.value = AuthState.Authenticated
        }
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun signUp() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signUp(_email.value, _password.value)
                _authState.value = AuthState.Authenticated
            } catch (e : Exception){
                _authState.value = AuthState.Error(e.localizedMessage ?: "Sign Up Failed")
            }
        }
    }

    fun signIn(){
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signIn(_email.value,_password.value)
                _authState.value = AuthState.Authenticated
            } catch (e : Exception){
                _authState.value = AuthState.Error(e.localizedMessage ?: "Sign In failed")
            }
        }
    }
}
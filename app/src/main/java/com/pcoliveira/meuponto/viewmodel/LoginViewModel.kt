package com.pcoliveira.meuponto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val validUsername = "admin"
    private val validPassword = "1234"

    fun login() {
        val user = username.value.orEmpty()
        val pass = password.value.orEmpty()

        if (user.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Preencha todos os campos."
            _loginSuccess.value = false
        } else if (user == validUsername && pass == validPassword) {
            _errorMessage.value = null
            _loginSuccess.value = true
        } else {
            _errorMessage.value = "Usuário ou senha inválidos."
            _loginSuccess.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

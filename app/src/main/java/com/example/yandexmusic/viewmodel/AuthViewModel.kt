package com.example.yandexmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexmusic.data.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(repository.getCurrentUser())
    val user: StateFlow<FirebaseUser?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signIn(email, password)
            result.onSuccess {
                _user.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
            _loading.value = false
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signUp(email, password)
            result.onSuccess {
                _user.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
            _loading.value = false
        }
    }

    fun signOut() {
        repository.signOut()
        _user.value = null
    }
}

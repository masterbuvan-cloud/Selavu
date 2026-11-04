package com.selavu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selavu.app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val age: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationSuccess: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUserId: String = "",
    val currentUserEmail: String = "",
    val currentUserAge: Int = 0
)

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        val loggedIn = userRepository.isUserLoggedIn()
        if (loggedIn) {
            val uid = userRepository.getLoggedInUserId()
            val email = userRepository.getLoggedInUserEmail()

            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    currentUserId = uid,
                    currentUserEmail = email
                )
            }

            // Load extra profile details (e.g. age) from Firestore asynchronously
            viewModelScope.launch {
                val profile = userRepository.getUserProfile(uid)
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            currentUserAge = profile.age,
                            currentUserEmail = profile.email.ifEmpty { email }
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    isLoggedIn = false,
                    currentUserId = "",
                    currentUserEmail = "",
                    currentUserAge = 0
                )
            }
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onAgeChange(newAge: String) {
        val filtered = newAge.filter { it.isDigit() }
        _uiState.update { it.copy(age = filtered, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = newConfirmPassword, errorMessage = null) }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null,
                isRegistrationSuccess = false,
                isLoginSuccess = false
            )
        }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = userRepository.register(
                email = state.email,
                ageStr = state.age,
                password = state.password,
                confirmPass = state.confirmPassword
            )

            result.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistrationSuccess = true,
                            successMessage = "Registration successful! Welcome to Selavu.",
                            errorMessage = null,
                            password = "",
                            confirmPassword = ""
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Registration failed."
                        )
                    }
                }
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = userRepository.login(
                email = state.email,
                password = state.password
            )

            result.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = true,
                            isLoggedIn = true,
                            currentUserId = profile.uid,
                            currentUserEmail = profile.email,
                            currentUserAge = profile.age,
                            errorMessage = null,
                            password = ""
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Invalid email or password."
                        )
                    }
                }
            )
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        userRepository.logout()
        _uiState.value = AuthUiState(
            isLoggedIn = false,
            currentUserId = "",
            currentUserEmail = "",
            currentUserAge = 0
        )
        onLogoutComplete()
    }
}

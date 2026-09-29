package com.watermeter.equipmentrecorder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watermeter.equipmentrecorder.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navigateToHome: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _employeeNumber = MutableStateFlow("")
    val employeeNumber: StateFlow<String> = _employeeNumber.asStateFlow()

    fun onEmployeeNumberChanged(number: String) {
        _employeeNumber.value = number
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate network delay
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            val number = _employeeNumber.value
            // Fake authentication: accept any 6-digit number as operator, "000000" as admin
            if (number.length == 6 && number.all { it.isDigit() }) {
                if (number == "000000") {
                    // Admin
                    navigateToHome()
                } else {
                    // Operator
                    navigateToHome()
                }
            } else {
                _uiState.update { it.copy(error = "Nomor Induk Karyawan tidak valid") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEmployeeNumberCleared() {
        _employeeNumber.value = ""
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
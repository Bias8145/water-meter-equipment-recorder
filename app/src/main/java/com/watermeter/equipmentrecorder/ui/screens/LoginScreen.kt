package com.watermeter.equipmentrecorder.ui.screens

import androidx.compose.foundation.Clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.keyboard.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.watermeter.equipmentrecorder.R
import com.watermeter.equipmentrecorder.ui.theme.WaterMeterEquipmentRecorderTheme
import com.watermeter.equipmentrecorder.ui.viewmodel.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory { /* TODO: pass navigation */ }),
    // We'll pass navigation from the NavHost controller
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val employeeNumber by viewModel.employeeNumber.collectAsState()

    WaterMeterEquipmentRecorderTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = employeeNumber,
                onValueChange = { viewModel.onEmployeeNumberChanged(it) },
                label = { Text("Nomor Induk Karyawan") },
                placeholder = { Text("Masukkan 6 digit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.error != null,
                errorText = if (uiState.error != null) {
                    Text(uiState.error!!)
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (uiState.error == null) {
                        viewModel.onLoginClicked()
                    }
                },
                enabled = employeeNumber.length == 6,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Masuk")
                }
            }
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Factory for ViewModel that accepts a navigation lambda
class LoginViewModelFactory(
    private val onLoginSuccess: () -> Unit
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(onLoginSuccess) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.watermeter.equipmentrecorder.ui.watermeter

import android.content.Context
import androidx.compose.foundation.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.updateText
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watermeter.equipmentrecorder.data.local.AppDatabase
import com.watermeter.equipmentrecorder.data.repository.WaterMeterRepository
import com.watermeter.equipmentrecorder.ui.theme.WaterMeterEquipmentRecorderTheme
import com.watermeter.equipmentrecorder.data.model.WaterMeterCheckpoint
import kotlinx.coroutines.flow.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext

@Composable
fun WaterMeterScreen(
    onNavigateBack: () -> Unit, // For navigating back to home
    operatorId: Long,
    templateId: Long,
    viewModelFactory: WaterMeterViewModel.Factory = WaterMeterViewModel.Factory(
        repository = WaterMeterRepository(AppDatabase.getInstance(LocalContext.current)),
        operatorId = operatorId,
        templateId = templateId
    )
) {
    val viewModel: WaterMeterViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pencatatan Water Meter") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
        ) {
            // Checkpoint selection
            Text(
                text = "Pilih Checkpoint",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Loading state for checkpoints
            if (uiState.isLoadingCheckpoints && uiState.checkpoints.isEmpty()) {
                // Show loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            } 
            // Error state for checkpoints
            else if (uiState.checkpointError != null && uiState.checkpoints.isEmpty()) {
                // Show error with retry
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gagal memuat checkpoint",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = uiState.checkpointError,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Retry loading checkpoints
                            viewModel.selectCheckpoint(viewModel.uiState.value.selectedCheckpointId ?: 0)
                        }
                    ) {
                        Text("Coba Lagi")
                    }
                }
            }
            // Empty state for checkpoints
            else if (uiState.checkpoints.isEmpty()) {
                // Show empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tidak ada checkpoint tersedia",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Silakan tambah checkpoint melalui pengaturan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Normal state - show checkpoints
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    items(uiState.checkpoints) { checkpoint ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clickable {
                                    viewModel.selectCheckpoint(checkpoint.id)
                                },
                            shape = RoundedCornerShape(4.dp),
                            backgroundColor = if (uiState.selectedCheckpointId == checkpoint.id) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ) {
                            Text(
                                text = checkpoint.name,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // If a checkpoint is selected, show details
            if (uiState.selectedCheckpointId != null) {
                val selectedCheckpoint = uiState.checkpoints.firstOrNull { it.id == uiState.selectedCheckpointId }
                if (selectedCheckpoint != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Checkpoint: ${selectedCheckpoint.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        // Loading state for previous reading
                        if (uiState.isLoadingPrevious && uiState.previousReading == null) {
                            // Show loading indicator for previous reading
                            Text(
                                text = "Memuat pembacaan sebelumnya...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                        }
                        // Error state for previous reading
                        else if (uiState.previousError != null && uiState.previousReading == null) {
                            // Show error with retry for previous reading
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Gagal memuat pembacaan sebelumnya",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = uiState.previousError,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        // Retry loading previous reading
                                        viewModel.selectCheckpoint(uiState.selectedCheckpointId ?: 0)
                                    }
                                ) {
                                    Text("Coba Lagi")
                                }
                            }
                        }
                        // Normal state - show previous reading
                        else {
                            // Previous reading
                            val previousReading = uiState.previousReading
                            Text(
                                text = "Previous Reading: ${if (previousReading != null) previousReading.toString() else "Belum ada"}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )

                            // Current reading input
                            OutlinedTextField(
                                value = TextFieldValue(uiState.currentReading),
                                onValueChange = { viewModel.updateCurrentReading(it.text) },
                                label = { Text("Current Reading") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.saveError != null && !uiState.saveError.isNullOrBlank(),
                                errorText = { if (uiState.saveError != null) Text(uiState.saveError) else null },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )

                            // Usage calculation
                            val usage = uiState.usage
                            when {
                                usage == null -> {
                                    Text(
                                        text = "Usage: Tidak dapat dihitung (tidak ada previous reading)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    )
                                }
                                usage!! < 0 -> {
                                    Text(
                                        text = "Usage: ${usage} (PERINGATAN: Pengurangan meter!)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Usage: ${usage}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    )
                                }
                            }

                            // Save button
                            Button(
                                onClick = {
                                    if (uiState.currentReading.isNotBlank() && uiState.selectedCheckpointId != null) {
                                        viewModel.saveReading()
                                    }
                                },
                                enabled = uiState.currentReading.isNotBlank() && uiState.selectedCheckpointId != null && !uiState.isSaving,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else if (uiState.saveSuccess) {
                                    // Show success checkmark
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Berhasil",
                                        tint = MaterialTheme.colorScheme.success,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text("Simpan")
                                }
                            }
                        }
                    }
                }

                // Show error if any (outside of the card)
                if (uiState.saveError != null && uiState.saveError.isNotBlank() && !uiState.isSaving) {
                    Text(
                        text = uiState.saveError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

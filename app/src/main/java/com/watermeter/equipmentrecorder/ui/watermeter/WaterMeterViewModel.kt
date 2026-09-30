package com.watermeter.equipmentrecorder.ui.watermeter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watermeter.equipmentrecorder.data.repository.WaterMeterRepository
import com.watermeter.equipmentrecorder.data.model.WaterMeterCheckpoint
import com.watermeter.equipmentrecorder.data.model.WaterMeterReading
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WaterMeterViewModel(
    private val repository: WaterMeterRepository,
    private val operatorId: Long,
    private val templateId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        // Load checkpoints for the template
        viewModelScope.launch {
            try {
                repository.getCheckpointsByTemplateId(templateId)
                    .collect { checkpoints ->
                        _uiState.update { it.copy(
                            checkpoints = checkpoints,
                            isLoadingCheckpoints = false
                        ) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    checkpointError = "Failed to load checkpoints: ${e.message}",
                    isLoadingCheckpoints = false
                ) }
            }
        }
    }

    // Select a checkpoint
    fun selectCheckpoint(checkpointId: Long) {
        _uiState.update { it.copy(selectedCheckpointId = checkpointId) }
        // Load previous reading when checkpoint is selected
        if (checkpointId != null) {
            viewModelScope.launch {
                try {
                    val latestReading = repository.getLatestReadingByCheckpointId(checkpointId)
                        .firstOrNull() // Get the latest or null if none
                    
                    _uiState.update { it.copy(
                        previousReading = latestReading?.currentReading,
                        isLoadingPrevious = false
                    ) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(
                        previousError = "Failed to load previous reading: ${e.message}",
                        isLoadingPrevious = false
                    ) }
                }
            }
        }
    }

    // Update current reading input
    fun updateCurrentReading(reading: String) {
        _uiState.update { it.copy(currentReading = reading) }
    }

    // Save the reading
    fun saveReading() {
        val state = _uiState.value
        val checkpointId = state.selectedCheckpointId
        val currentReadingStr = state.currentReading

        if (checkpointId == null) {
            _uiState.update { it.copy(saveError = "Please select a checkpoint") }
            return
        }

        if (currentReadingStr.isBlank()) {
            _uiState.update { it.copy(saveError = "Current reading is required") }
            return
        }

        val currentReading = currentReadingStr.toLongOrNull()
        if (currentReading == null) {
            _uiState.update { it.copy(saveError = "Current reading must be a number") }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveError = null) }

        viewModelScope.launch {
            try {
                // Get the latest reading for this checkpoint to calculate usage
                val latestReading = repository.getLatestReadingByCheckpointId(checkpointId)
                    .first() // Since we only need the latest

                val previousReading = latestReading?.currentReading
                val usage = if (previousReading != null) {
                    currentReading - previousReading
                } else {
                    null // No previous reading, usage cannot be calculated
                }

                // Validate usage: warn if negative or zero (rollback or no usage)
                val validationStatus = if (usage != null && usage < 0) {
                    "ROLLBACK_WARNING"
                } else if (usage != null && usage == 0) {
                    "ZERO_USAGE_WARNING"
                } else {
                    "VALID"
                }

                val readingToSave = WaterMeterReading(
                    id = 0, // Will be auto-generated
                    checkpointId = checkpointId,
                    previousReading = previousReading,
                    currentReading = currentReading,
                    usage = usage,
                    readingTimestamp = System.currentTimeMillis(),
                    inputMethod = "MANUAL",
                    ocrConfidence = null,
                    photoId = null,
                    validationStatus = validationStatus,
                    operatorId = operatorId,
                    notes = null
                )

                val readingId = repository.saveReading(readingToSave)

                // Reset input after successful save and show success
                _uiState.update { it.copy(
                    isSaving = false,
                    currentReading = "",
                    saveError = null,
                    saveSuccess = true
                )}
                
                // Reset success flag after short delay (for UI feedback)
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000) // 2 seconds
                    _uiState.update { it.copy(saveSuccess = false) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    saveError = "Failed to save reading: ${e.message}"
                )}
            }
        }
    }

    data class UiState(
        val checkpoints: List<WaterMeterCheckpoint> = emptyList(),
        val selectedCheckpointId: Long? = null,
        val currentReading: String = "",
        val previousReading: Long? = null,
        val usage: Long? = null,
        val isSaving: Boolean = false,
        val saveError: String? = null,
        val saveSuccess: Boolean = false,
        val isLoadingCheckpoints: Boolean = true,
        val checkpointError: String? = null,
        val isLoadingPrevious: Boolean = false,
        val previousError: String? = null
    )

    // Factory for creating the ViewModel
    class Factory(
        private val repository: WaterMeterRepository,
        private val operatorId: Long,
        private val templateId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WaterMeterViewModel(repository, operatorId, templateId) as T
        }
    }
}

package com.rio.rostry.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.service.BackupService
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Backup and Restore functionality.
 */
@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val backupService: BackupService
) : ViewModel() {
    
    // Backup state
    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState = _backupState.asStateFlow()
    
    // Restore state
    private val _restoreState = MutableStateFlow<RestoreState>(RestoreState.Idle)
    val restoreState = _restoreState.asStateFlow()
    
    // Available backups
    private val _backups = MutableStateFlow<List<BackupService.BackupMetadata>>(emptyList())
    val backups = _backups.asStateFlow()
    
    // Validation result for pending import
    private val _pendingImportMetadata = MutableStateFlow<BackupService.BackupMetadata?>(null)
    val pendingImportMetadata = _pendingImportMetadata.asStateFlow()
    
    // URI of file being imported
    private val _pendingImportUri = MutableStateFlow<Uri?>(null)
    
    init {
        loadBackupHistory()
    }
    
    /**
     * Load backup history from cache.
     */
    fun loadBackupHistory() {
        viewModelScope.launch {
            val backupList = backupService.listBackups()
            _backups.value = backupList
        }
    }
    
    /**
     * Create a new backup.
     */
    fun createBackup() {
        viewModelScope.launch {
            _backupState.value = BackupState.InProgress
            
            when (val result = backupService.exportToDownloads()) {
                is Resource.Success -> {
                    result.data?.let { uri ->
                        _backupState.value = BackupState.Success(uri)
                        loadBackupHistory() // Refresh list
                    } ?: run {
                        _backupState.value = BackupState.Error("Backup succeeded but no URI returned")
                    }
                }
                is Resource.Error -> {
                    _backupState.value = BackupState.Error(result.message ?: "Backup failed")
                }
                is Resource.Loading -> {
                    _backupState.value = BackupState.InProgress
                }
            }
        }
    }
    
    /**
     * Validate a backup file before import.
     */
    fun validateBackup(uri: Uri) {
        viewModelScope.launch {
            _restoreState.value = RestoreState.Validating
            
            when (val result = backupService.validateBackup(uri)) {
                is Resource.Success -> {
                    result.data?.let { metadata ->
                        _pendingImportUri.value = uri
                        _pendingImportMetadata.value = metadata
                        _restoreState.value = RestoreState.PendingConfirmation(metadata)
                    } ?: run {
                        _restoreState.value = RestoreState.Error("Invalid backup metadata")
                    }
                }
                is Resource.Error -> {
                    _restoreState.value = RestoreState.Error(result.message ?: "Invalid backup")
                }
                is Resource.Loading -> {
                    _restoreState.value = RestoreState.Validating
                }
            }
        }
    }
    
    /**
     * Confirm and execute restore.
     */
    fun confirmRestore() {
        val uri = _pendingImportUri.value ?: return
        
        viewModelScope.launch {
            _restoreState.value = RestoreState.Restoring
            
            when (val result = backupService.importBackup(uri)) {
                is Resource.Success -> {
                    result.data?.let { importResult ->
                        _restoreState.value = RestoreState.Success(importResult)
                        _pendingImportUri.value = null
                        _pendingImportMetadata.value = null
                    } ?: run {
                        _restoreState.value = RestoreState.Error("Import completed but no result returned")
                    }
                }
                is Resource.Error -> {
                    _restoreState.value = RestoreState.Error(result.message ?: "Restore failed")
                }
                is Resource.Loading -> {
                    _restoreState.value = RestoreState.Restoring
                }
            }
        }
    }
    
    /**
     * Cancel pending restore.
     */
    fun cancelRestore() {
        _pendingImportUri.value = null
        _pendingImportMetadata.value = null
        _restoreState.value = RestoreState.Idle
    }
    
    /**
     * Reset backup state.
     */
    fun resetBackupState() {
        _backupState.value = BackupState.Idle
    }
    
    /**
     * Reset restore state.
     */
    fun resetRestoreState() {
        _restoreState.value = RestoreState.Idle
    }
    
    /**
     * Clear all cached exports.
     */
    fun clearExports() {
        backupService.clearExports()
        loadBackupHistory()
    }
}

sealed class BackupState {
    data object Idle : BackupState()
    data object InProgress : BackupState()
    data class Success(val uri: Uri) : BackupState()
    data class Error(val message: String) : BackupState()
}

sealed class RestoreState {
    data object Idle : RestoreState()
    data object Validating : RestoreState()
    data class PendingConfirmation(val metadata: BackupService.BackupMetadata) : RestoreState()
    data object Restoring : RestoreState()
    data class Success(val result: BackupService.ImportResult) : RestoreState()
    data class Error(val message: String) : RestoreState()
}

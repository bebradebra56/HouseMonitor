package com.housemo.monisto.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.data.repo.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefsManager: PreferencesManager,
    private val activityRepo: ActivityRepository
) : ViewModel() {

    val unitSystem: StateFlow<String> = prefsManager.unitSystem
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Metric")

    val notificationsEnabled: StateFlow<Boolean> = prefsManager.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val defaultCurrency: StateFlow<String> = prefsManager.defaultCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")

    fun setUnitSystem(value: String) {
        viewModelScope.launch { prefsManager.setUnitSystem(value) }
    }

    fun setNotificationsEnabled(value: Boolean) {
        viewModelScope.launch { prefsManager.setNotificationsEnabled(value) }
    }

    fun setDefaultCurrency(value: String) {
        viewModelScope.launch { prefsManager.setDefaultCurrency(value) }
    }

    fun clearActivityLog() {
        viewModelScope.launch {
            activityRepo.logAction("Settings", "Activity log cleared")
        }
    }
}

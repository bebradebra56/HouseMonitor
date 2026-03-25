package com.housemo.monisto.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.ActivityLog
import com.housemo.monisto.data.repo.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ActivityHistoryViewModel(
    private val activityRepo: ActivityRepository
) : ViewModel() {
    val logs: StateFlow<List<ActivityLog>> = activityRepo.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

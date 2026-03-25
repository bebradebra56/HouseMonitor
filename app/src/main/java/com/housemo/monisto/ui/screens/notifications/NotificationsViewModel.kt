package com.housemo.monisto.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.Inspection
import com.housemo.monisto.data.local.entity.Repair
import com.housemo.monisto.data.repo.InspectionRepository
import com.housemo.monisto.data.repo.RepairRepository
import com.housemo.monisto.util.DateUtils
import kotlinx.coroutines.flow.*

data class AppNotification(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isUrgent: Boolean = false
)

class NotificationsViewModel(
    private val inspectionRepo: InspectionRepository,
    private val repairRepo: RepairRepository
) : ViewModel() {

    val notifications: StateFlow<List<AppNotification>> = combine(
        repairRepo.getScheduledRepairs(),
        inspectionRepo.getOpenIssues()
    ) { repairs, issues ->
        val notifs = mutableListOf<AppNotification>()
        repairs.forEach { repair ->
            val overdue = DateUtils.isOverdue(repair.scheduledDate)
            val dueSoon = DateUtils.isDueSoon(repair.scheduledDate)
            if (overdue || dueSoon) {
                notifs.add(
                    AppNotification(
                        id = repair.id,
                        title = if (overdue) "Repair Overdue" else "Repair Due Soon",
                        message = "${repair.type} — ${repair.scheduledDate?.let { DateUtils.formatDate(it) } ?: "No date"}",
                        type = "Repair",
                        timestamp = repair.scheduledDate ?: repair.createdAt,
                        isUrgent = overdue
                    )
                )
            }
        }
        issues.filter { it.severity == "Critical" }.forEach { issue ->
            notifs.add(
                AppNotification(
                    id = issue.id,
                    title = "Critical Issue",
                    message = "${issue.type} at ${issue.location} — needs immediate attention",
                    type = "Issue",
                    timestamp = issue.createdAt,
                    isUrgent = true
                )
            )
        }
        notifs.sortedByDescending { it.isUrgent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

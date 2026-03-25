package com.housemo.monisto.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.Building
import com.housemo.monisto.data.local.entity.Issue
import com.housemo.monisto.data.local.entity.Repair
import com.housemo.monisto.data.repo.BuildingRepository
import com.housemo.monisto.data.repo.InspectionRepository
import com.housemo.monisto.data.repo.RepairRepository
import kotlinx.coroutines.flow.*

data class ReportStats(
    val totalBuildings: Int = 0,
    val totalInspections: Int = 0,
    val openIssues: Int = 0,
    val pendingRepairs: Int = 0,
    val criticalIssues: Int = 0,
    val avgConditionScore: Float = 0f
)

class ReportViewModel(
    private val buildingRepo: BuildingRepository,
    private val inspectionRepo: InspectionRepository,
    private val repairRepo: RepairRepository
) : ViewModel() {

    val buildings: StateFlow<List<Building>> = buildingRepo.getAllBuildings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openIssues: StateFlow<List<Issue>> = inspectionRepo.getOpenIssues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRepairs: StateFlow<List<Repair>> = repairRepo.getAllRepairs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<ReportStats> = combine(
        buildingRepo.getAllBuildings(),
        inspectionRepo.getInspectionCount(),
        inspectionRepo.getOpenIssueCount(),
        repairRepo.getPendingRepairCount(),
        inspectionRepo.getOpenIssues()
    ) { buildings, inspCount, openIssues, pendingRepairs, issues ->
        ReportStats(
            totalBuildings = buildings.size,
            totalInspections = inspCount,
            openIssues = openIssues,
            pendingRepairs = pendingRepairs,
            criticalIssues = issues.count { it.severity == "Critical" },
            avgConditionScore = if (buildings.isEmpty()) 0f else buildings.map { it.conditionScore }.average().toFloat()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportStats())
}

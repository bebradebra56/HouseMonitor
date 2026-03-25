package com.housemo.monisto.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import com.housemo.monisto.data.local.entity.Building
import com.housemo.monisto.data.local.entity.Inspection
import com.housemo.monisto.data.local.entity.Issue
import com.housemo.monisto.data.repo.ActivityRepository
import com.housemo.monisto.data.repo.BuildingRepository
import com.housemo.monisto.data.repo.InspectionRepository
import com.housemo.monisto.data.repo.RepairRepository
import kotlinx.coroutines.flow.Flow

class DashboardViewModel(
    private val buildingRepo: BuildingRepository,
    private val inspectionRepo: InspectionRepository,
    private val repairRepo: RepairRepository,
    private val activityRepo: ActivityRepository
) : ViewModel() {
    val buildingCount: Flow<Int> = buildingRepo.getBuildingCount()
    val openIssueCount: Flow<Int> = inspectionRepo.getOpenIssueCount()
    val recentInspections: Flow<List<Inspection>> = inspectionRepo.getRecentInspections()
    val pendingRepairCount: Flow<Int> = repairRepo.getPendingRepairCount()
    val buildings: Flow<List<Building>> = buildingRepo.getAllBuildings()
    val openIssues: Flow<List<Issue>> = inspectionRepo.getOpenIssues()
}

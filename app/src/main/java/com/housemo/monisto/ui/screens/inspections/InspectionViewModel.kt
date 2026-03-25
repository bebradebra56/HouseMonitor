package com.housemo.monisto.ui.screens.inspections

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.*
import com.housemo.monisto.data.repo.ActivityRepository
import com.housemo.monisto.data.repo.BuildingRepository
import com.housemo.monisto.data.repo.InspectionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InspectionViewModel(
    private val repository: InspectionRepository,
    private val buildingRepository: BuildingRepository,
    private val activityRepo: ActivityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val buildingId: Long = savedStateHandle.get<Long>("buildingId") ?: -1L
    private val inspectionId: Long = savedStateHandle.get<Long>("inspectionId") ?: -1L
    private val issueId: Long = savedStateHandle.get<Long>("issueId") ?: -1L

    val allInspections: StateFlow<List<Inspection>> = repository.getAllInspections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspections: StateFlow<List<Inspection>> = if (buildingId != -1L)
        repository.getInspectionsByBuilding(buildingId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else repository.getAllInspections().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentInspection: StateFlow<Inspection?> = if (inspectionId != -1L)
        repository.getInspectionById(inspectionId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val issues: StateFlow<List<Issue>> = if (inspectionId != -1L)
        repository.getIssuesByInspection(inspectionId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    val currentIssue: StateFlow<Issue?> = if (issueId != -1L)
        repository.getIssueById(issueId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val openIssues: StateFlow<List<Issue>> = repository.getOpenIssues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val measurements: StateFlow<List<Measurement>> = if (issueId != -1L)
        repository.getMeasurementsByIssue(issueId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    fun addInspection(date: Long, inspector: String, notes: String, overallCondition: String, bId: Long = buildingId) {
        viewModelScope.launch {
            val id = repository.insertInspection(Inspection(buildingId = bId, date = date, inspector = inspector, notes = notes, overallCondition = overallCondition))
            activityRepo.logAction("Inspection Added", "Inspection by $inspector", "Inspection", id)
        }
    }

    fun updateInspection(inspection: Inspection) {
        viewModelScope.launch { repository.updateInspection(inspection) }
    }

    fun deleteInspection(inspection: Inspection) {
        viewModelScope.launch { repository.deleteInspection(inspection) }
    }

    fun addIssue(type: String, location: String, severity: String, description: String, iId: Long = inspectionId) {
        viewModelScope.launch {
            val id = repository.insertIssue(Issue(inspectionId = iId, type = type, location = location, severity = severity, description = description))
            activityRepo.logAction("Issue Added", "$type issue at $location", "Issue", id)
            refreshBuildingScore(iId)
        }
    }

    fun updateIssue(issue: Issue) {
        viewModelScope.launch {
            repository.updateIssue(issue)
            refreshBuildingScore(issue.inspectionId)
        }
    }

    fun resolveIssue(issue: Issue) {
        viewModelScope.launch {
            repository.updateIssue(issue.copy(isResolved = true, resolvedAt = System.currentTimeMillis()))
            activityRepo.logAction("Issue Resolved", "${issue.type} issue resolved", "Issue", issue.id)
            refreshBuildingScore(issue.inspectionId)
        }
    }

    fun deleteIssue(issue: Issue) {
        viewModelScope.launch {
            repository.deleteIssue(issue)
            refreshBuildingScore(issue.inspectionId)
        }
    }

    fun addMeasurement(type: String, value: Float, unit: String, notes: String, iId: Long = issueId) {
        viewModelScope.launch {
            repository.insertMeasurement(Measurement(issueId = iId, type = type, value = value, unit = unit, notes = notes))
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch { repository.deleteMeasurement(measurement) }
    }

    private suspend fun refreshBuildingScore(iId: Long) {
        val inspection = repository.getInspectionByIdOnce(iId) ?: return
        val bId = inspection.buildingId
        val issues = repository.getOpenIssuesByBuildingOnce(bId)
        val score = calculateConditionScore(issues.map { it.severity })
        buildingRepository.updateConditionScore(bId, score)
    }

    private fun calculateConditionScore(severities: List<String>): Float {
        val penalty = severities.sumOf { s ->
            when (s) {
                "Critical" -> 20
                "High" -> 10
                "Medium" -> 5
                else -> 2
            }
        }
        return maxOf(0f, 100f - penalty)
    }
}

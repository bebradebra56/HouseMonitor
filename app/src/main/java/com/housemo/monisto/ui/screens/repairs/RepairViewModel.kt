package com.housemo.monisto.ui.screens.repairs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.*
import com.housemo.monisto.data.repo.ActivityRepository
import com.housemo.monisto.data.repo.RepairRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RepairViewModel(
    private val repository: RepairRepository,
    private val activityRepo: ActivityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val issueId: Long = savedStateHandle.get<Long>("issueId") ?: -1L
    private val repairId: Long = savedStateHandle.get<Long>("repairId") ?: -1L

    val allRepairs: StateFlow<List<Repair>> = repository.getAllRepairs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairs: StateFlow<List<Repair>> = if (issueId != -1L)
        repository.getRepairsByIssue(issueId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else repository.getAllRepairs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentRepair: StateFlow<Repair?> = if (repairId != -1L)
        repository.getRepairById(repairId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val scheduledRepairs: StateFlow<List<Repair>> = repository.getScheduledRepairs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = if (repairId != -1L)
        repository.getTasksByRepair(repairId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    val allMaterials: StateFlow<List<Material>> = repository.getAllMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRepair(type: String, description: String, cost: Double, priority: String, scheduledDate: Long?, contractor: String, iId: Long = issueId) {
        viewModelScope.launch {
            val id = repository.insertRepair(Repair(issueId = iId, type = type, description = description, cost = cost, priority = priority, scheduledDate = scheduledDate, contractor = contractor))
            activityRepo.logAction("Repair Scheduled", "Repair: $type", "Repair", id)
        }
    }

    fun updateRepair(repair: Repair) {
        viewModelScope.launch { repository.updateRepair(repair) }
    }

    fun completeRepair(repair: Repair) {
        viewModelScope.launch {
            repository.updateRepair(repair.copy(status = "Done", completedDate = System.currentTimeMillis()))
            activityRepo.logAction("Repair Completed", "Completed repair: ${repair.type}", "Repair", repair.id)
        }
    }

    fun deleteRepair(repair: Repair) {
        viewModelScope.launch { repository.deleteRepair(repair) }
    }

    fun addTask(description: String, dueDate: Long?, rId: Long = repairId) {
        viewModelScope.launch {
            repository.insertTask(Task(repairId = rId, description = description, dueDate = dueDate))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status == "Done") "Pending" else "Done"
            repository.updateTask(task.copy(status = newStatus, completedAt = if (newStatus == "Done") System.currentTimeMillis() else null))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun addMaterial(name: String, type: String, description: String, quantity: Float, unit: String, supplier: String) {
        viewModelScope.launch {
            repository.insertMaterial(Material(name = name, type = type, description = description, quantity = quantity, unit = unit, supplier = supplier))
            activityRepo.logAction("Material Added", "Added material: $name", "Material", 0)
        }
    }

    fun updateMaterial(material: Material) {
        viewModelScope.launch { repository.updateMaterial(material) }
    }

    fun deleteMaterial(material: Material) {
        viewModelScope.launch { repository.deleteMaterial(material) }
    }
}

package com.housemo.monisto.ui.screens.buildings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.*
import com.housemo.monisto.data.repo.ActivityRepository
import com.housemo.monisto.data.repo.BuildingRepository
import com.housemo.monisto.data.repo.InspectionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BuildingViewModel(
    private val repository: BuildingRepository,
    private val inspectionRepository: InspectionRepository,
    private val activityRepo: ActivityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val buildingId: Long = savedStateHandle.get<Long>("buildingId") ?: -1L
    private val floorId: Long = savedStateHandle.get<Long>("floorId") ?: -1L
    private val roomId: Long = savedStateHandle.get<Long>("roomId") ?: -1L
    private val structureId: Long = savedStateHandle.get<Long>("structureId") ?: -1L

    val buildings: StateFlow<List<Building>> = repository.getAllBuildings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentBuilding: StateFlow<Building?> = if (buildingId != -1L)
        repository.getBuildingById(buildingId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val floors: StateFlow<List<Floor>> = if (buildingId != -1L)
        repository.getFloorsByBuilding(buildingId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    val currentFloor: StateFlow<Floor?> = if (floorId != -1L)
        repository.getFloorById(floorId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val rooms: StateFlow<List<Room>> = if (floorId != -1L)
        repository.getRoomsByFloor(floorId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    val currentRoom: StateFlow<Room?> = if (roomId != -1L)
        repository.getRoomById(roomId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    val structures: StateFlow<List<Structure>> = if (roomId != -1L)
        repository.getStructuresByRoom(roomId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    val currentStructure: StateFlow<Structure?> = if (structureId != -1L)
        repository.getStructureById(structureId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    else MutableStateFlow(null)

    fun addBuilding(name: String, address: String, floorsCount: Int, yearBuilt: Int, notes: String) {
        viewModelScope.launch {
            val id = repository.insertBuilding(Building(name = name, address = address, floorsCount = floorsCount, yearBuilt = yearBuilt, notes = notes))
            activityRepo.logAction("Building Added", "Added building: $name", "Building", id)
        }
    }

    fun updateBuilding(building: Building) {
        viewModelScope.launch {
            repository.updateBuilding(building)
            activityRepo.logAction("Building Updated", "Updated building: ${building.name}", "Building", building.id)
        }
    }

    fun deleteBuilding(building: Building) {
        viewModelScope.launch {
            repository.deleteBuilding(building)
            activityRepo.logAction("Building Deleted", "Deleted building: ${building.name}", "Building", building.id)
        }
    }

    fun addFloor(name: String, level: Int) {
        viewModelScope.launch {
            val id = repository.insertFloor(Floor(buildingId = buildingId, name = name, level = level))
            activityRepo.logAction("Floor Added", "Added floor: $name", "Floor", id)
        }
    }

    fun updateFloor(floor: Floor) {
        viewModelScope.launch { repository.updateFloor(floor) }
    }

    fun deleteFloor(floor: Floor) {
        viewModelScope.launch { repository.deleteFloor(floor) }
    }

    fun addRoom(name: String, area: Float, purpose: String) {
        viewModelScope.launch {
            val id = repository.insertRoom(Room(floorId = floorId, name = name, area = area, purpose = purpose))
            activityRepo.logAction("Room Added", "Added room: $name", "Room", id)
        }
    }

    fun updateRoom(room: Room) {
        viewModelScope.launch { repository.updateRoom(room) }
    }

    fun deleteRoom(room: Room) {
        viewModelScope.launch { repository.deleteRoom(room) }
    }

    fun addStructure(type: String, material: String, condition: String, notes: String) {
        viewModelScope.launch {
            repository.insertStructure(Structure(roomId = roomId, type = type, material = material, condition = condition, notes = notes))
        }
    }

    fun updateStructure(structure: Structure) {
        viewModelScope.launch { repository.updateStructure(structure) }
    }

    fun deleteStructure(structure: Structure) {
        viewModelScope.launch { repository.deleteStructure(structure) }
    }

    fun refreshConditionScore(bId: Long = buildingId) {
        if (bId == -1L) return
        viewModelScope.launch {
            val issues = inspectionRepository.getOpenIssuesByBuildingOnce(bId)
            val score = calculateConditionScore(issues.map { it.severity })
            repository.updateConditionScore(bId, score)
        }
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

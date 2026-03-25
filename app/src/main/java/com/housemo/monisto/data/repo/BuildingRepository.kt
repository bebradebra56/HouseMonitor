package com.housemo.monisto.data.repo

import com.housemo.monisto.data.local.dao.*
import com.housemo.monisto.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class BuildingRepository(
    private val buildingDao: BuildingDao,
    private val floorDao: FloorDao,
    private val roomDao: RoomDao,
    private val structureDao: StructureDao
) {
    fun getAllBuildings(): Flow<List<Building>> = buildingDao.getAllBuildings()
    fun getBuildingById(id: Long): Flow<Building?> = buildingDao.getBuildingById(id)
    fun getBuildingCount(): Flow<Int> = buildingDao.getBuildingCount()
    suspend fun insertBuilding(building: Building): Long = buildingDao.insertBuilding(building)
    suspend fun updateBuilding(building: Building) = buildingDao.updateBuilding(building)
    suspend fun deleteBuilding(building: Building) = buildingDao.deleteBuilding(building)
    suspend fun updateConditionScore(id: Long, score: Float) = buildingDao.updateConditionScore(id, score)

    fun getFloorsByBuilding(buildingId: Long): Flow<List<Floor>> = floorDao.getFloorsByBuilding(buildingId)
    fun getFloorById(id: Long): Flow<Floor?> = floorDao.getFloorById(id)
    fun getFloorCountForBuilding(buildingId: Long): Flow<Int> = floorDao.getFloorCountForBuilding(buildingId)
    suspend fun insertFloor(floor: Floor): Long = floorDao.insertFloor(floor)
    suspend fun updateFloor(floor: Floor) = floorDao.updateFloor(floor)
    suspend fun deleteFloor(floor: Floor) = floorDao.deleteFloor(floor)

    fun getRoomsByFloor(floorId: Long): Flow<List<Room>> = roomDao.getRoomsByFloor(floorId)
    fun getRoomById(id: Long): Flow<Room?> = roomDao.getRoomById(id)
    fun getRoomCountForFloor(floorId: Long): Flow<Int> = roomDao.getRoomCountForFloor(floorId)
    suspend fun insertRoom(room: Room): Long = roomDao.insertRoom(room)
    suspend fun updateRoom(room: Room) = roomDao.updateRoom(room)
    suspend fun deleteRoom(room: Room) = roomDao.deleteRoom(room)

    fun getStructuresByRoom(roomId: Long): Flow<List<Structure>> = structureDao.getStructuresByRoom(roomId)
    fun getStructureById(id: Long): Flow<Structure?> = structureDao.getStructureById(id)
    suspend fun insertStructure(structure: Structure): Long = structureDao.insertStructure(structure)
    suspend fun updateStructure(structure: Structure) = structureDao.updateStructure(structure)
    suspend fun deleteStructure(structure: Structure) = structureDao.deleteStructure(structure)
}

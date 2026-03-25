package com.housemo.monisto.data.repo

import com.housemo.monisto.data.local.dao.RepairDao
import com.housemo.monisto.data.local.dao.TaskDao
import com.housemo.monisto.data.local.dao.MaterialDao
import com.housemo.monisto.data.local.entity.Material
import com.housemo.monisto.data.local.entity.Repair
import com.housemo.monisto.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

class RepairRepository(
    private val repairDao: RepairDao,
    private val taskDao: TaskDao,
    private val materialDao: MaterialDao
) {
    fun getAllRepairs(): Flow<List<Repair>> = repairDao.getAllRepairs()
    fun getRepairsByIssue(issueId: Long): Flow<List<Repair>> = repairDao.getRepairsByIssue(issueId)
    fun getRepairById(id: Long): Flow<Repair?> = repairDao.getRepairById(id)
    fun getPendingRepairs(): Flow<List<Repair>> = repairDao.getPendingRepairs()
    fun getScheduledRepairs(): Flow<List<Repair>> = repairDao.getScheduledRepairs()
    fun getPendingRepairCount(): Flow<Int> = repairDao.getPendingRepairCount()
    suspend fun insertRepair(repair: Repair): Long = repairDao.insertRepair(repair)
    suspend fun updateRepair(repair: Repair) = repairDao.updateRepair(repair)
    suspend fun deleteRepair(repair: Repair) = repairDao.deleteRepair(repair)

    fun getTasksByRepair(repairId: Long): Flow<List<Task>> = taskDao.getTasksByRepair(repairId)
    fun getPendingTasks(): Flow<List<Task>> = taskDao.getPendingTasks()
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    fun getAllMaterials(): Flow<List<Material>> = materialDao.getAllMaterials()
    fun getMaterialsByType(type: String): Flow<List<Material>> = materialDao.getMaterialsByType(type)
    suspend fun insertMaterial(material: Material): Long = materialDao.insertMaterial(material)
    suspend fun updateMaterial(material: Material) = materialDao.updateMaterial(material)
    suspend fun deleteMaterial(material: Material) = materialDao.deleteMaterial(material)
}

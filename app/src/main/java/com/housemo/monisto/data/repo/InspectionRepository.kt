package com.housemo.monisto.data.repo

import com.housemo.monisto.data.local.dao.*
import com.housemo.monisto.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class InspectionRepository(
    private val inspectionDao: InspectionDao,
    private val issueDao: IssueDao,
    private val measurementDao: MeasurementDao
) {
    fun getAllInspections(): Flow<List<Inspection>> = inspectionDao.getAllInspections()
    fun getInspectionsByBuilding(buildingId: Long): Flow<List<Inspection>> = inspectionDao.getInspectionsByBuilding(buildingId)
    fun getInspectionById(id: Long): Flow<Inspection?> = inspectionDao.getInspectionById(id)
    fun getInspectionCount(): Flow<Int> = inspectionDao.getInspectionCount()
    fun getRecentInspections(): Flow<List<Inspection>> = inspectionDao.getRecentInspections()
    suspend fun getInspectionByIdOnce(id: Long): Inspection? = inspectionDao.getInspectionByIdOnce(id)
    suspend fun insertInspection(inspection: Inspection): Long = inspectionDao.insertInspection(inspection)
    suspend fun updateInspection(inspection: Inspection) = inspectionDao.updateInspection(inspection)
    suspend fun deleteInspection(inspection: Inspection) = inspectionDao.deleteInspection(inspection)

    fun getAllIssues(): Flow<List<Issue>> = issueDao.getAllIssues()
    fun getIssuesByInspection(inspectionId: Long): Flow<List<Issue>> = issueDao.getIssuesByInspection(inspectionId)
    fun getIssueById(id: Long): Flow<Issue?> = issueDao.getIssueById(id)
    fun getOpenIssueCount(): Flow<Int> = issueDao.getOpenIssueCount()
    fun getOpenIssues(): Flow<List<Issue>> = issueDao.getOpenIssues()
    suspend fun insertIssue(issue: Issue): Long = issueDao.insertIssue(issue)
    suspend fun updateIssue(issue: Issue) = issueDao.updateIssue(issue)
    suspend fun deleteIssue(issue: Issue) = issueDao.deleteIssue(issue)
    suspend fun getOpenIssuesByBuildingOnce(buildingId: Long): List<Issue> = issueDao.getOpenIssuesByBuildingOnce(buildingId)

    fun getMeasurementsByIssue(issueId: Long): Flow<List<Measurement>> = measurementDao.getMeasurementsByIssue(issueId)
    suspend fun insertMeasurement(measurement: Measurement): Long = measurementDao.insertMeasurement(measurement)
    suspend fun updateMeasurement(measurement: Measurement) = measurementDao.updateMeasurement(measurement)
    suspend fun deleteMeasurement(measurement: Measurement) = measurementDao.deleteMeasurement(measurement)
}

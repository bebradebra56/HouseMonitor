package com.housemo.monisto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.housemo.monisto.data.local.dao.*
import com.housemo.monisto.data.local.entity.*

@Database(
    entities = [
        Building::class,
        Floor::class,
        Room::class,
        Structure::class,
        Inspection::class,
        Issue::class,
        Photo::class,
        Measurement::class,
        Material::class,
        Repair::class,
        Task::class,
        ActivityLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    abstract fun floorDao(): FloorDao
    abstract fun roomDao(): RoomDao
    abstract fun structureDao(): StructureDao
    abstract fun inspectionDao(): InspectionDao
    abstract fun issueDao(): IssueDao
    abstract fun photoDao(): PhotoDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun materialDao(): MaterialDao
    abstract fun repairDao(): RepairDao
    abstract fun taskDao(): TaskDao
    abstract fun activityLogDao(): ActivityLogDao
}

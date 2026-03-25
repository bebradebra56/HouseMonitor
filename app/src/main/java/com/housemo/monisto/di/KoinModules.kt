package com.housemo.monisto.di

import androidx.room.Room
import com.housemo.monisto.data.local.AppDatabase
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.data.repo.*
import com.housemo.monisto.ui.screens.buildings.BuildingViewModel
import com.housemo.monisto.ui.screens.dashboard.DashboardViewModel
import com.housemo.monisto.ui.screens.history.ActivityHistoryViewModel
import com.housemo.monisto.ui.screens.inspections.InspectionViewModel
import com.housemo.monisto.ui.screens.notifications.NotificationsViewModel
import com.housemo.monisto.ui.screens.photos.PhotoViewModel
import com.housemo.monisto.ui.screens.repairs.RepairViewModel
import com.housemo.monisto.ui.screens.reports.ReportViewModel
import com.housemo.monisto.ui.screens.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "house_monitor_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().buildingDao() }
    single { get<AppDatabase>().floorDao() }
    single { get<AppDatabase>().roomDao() }
    single { get<AppDatabase>().structureDao() }
    single { get<AppDatabase>().inspectionDao() }
    single { get<AppDatabase>().issueDao() }
    single { get<AppDatabase>().photoDao() }
    single { get<AppDatabase>().measurementDao() }
    single { get<AppDatabase>().materialDao() }
    single { get<AppDatabase>().repairDao() }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().activityLogDao() }
}

val repositoryModule = module {
    singleOf(::BuildingRepository)
    singleOf(::InspectionRepository)
    singleOf(::PhotoRepository)
    singleOf(::RepairRepository)
    singleOf(::ActivityRepository)
    single { PreferencesManager(androidContext()) }
}

val viewModelModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::BuildingViewModel)
    viewModelOf(::InspectionViewModel)
    viewModelOf(::PhotoViewModel)
    viewModelOf(::RepairViewModel)
    viewModelOf(::ReportViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ActivityHistoryViewModel)
    viewModelOf(::NotificationsViewModel)
}


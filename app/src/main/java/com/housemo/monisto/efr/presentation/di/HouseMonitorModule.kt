package com.housemo.monisto.efr.presentation.di

import com.housemo.monisto.efr.data.repo.HouseMonitorRepository
import com.housemo.monisto.efr.data.shar.HouseMonitorSharedPreference
import com.housemo.monisto.efr.data.utils.HouseMonitorPushToken
import com.housemo.monisto.efr.data.utils.HouseMonitorSystemService
import com.housemo.monisto.efr.domain.usecases.HouseMonitorGetAllUseCase
import com.housemo.monisto.efr.presentation.pushhandler.HouseMonitorPushHandler
import com.housemo.monisto.efr.presentation.ui.load.HouseMonitorLoadViewModel
import com.housemo.monisto.efr.presentation.ui.view.HouseMonitorViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val houseMonitorModule = module {
    factory {
        HouseMonitorPushHandler()
    }
    single {
        HouseMonitorRepository()
    }
    single {
        HouseMonitorSharedPreference(get())
    }
    factory {
        HouseMonitorPushToken()
    }
    factory {
        HouseMonitorSystemService(get())
    }
    factory {
        HouseMonitorGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        HouseMonitorViFun(get())
    }
    viewModel {
        HouseMonitorLoadViewModel(get(), get(), get())
    }
}
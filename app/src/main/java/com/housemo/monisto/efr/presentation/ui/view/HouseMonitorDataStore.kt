package com.housemo.monisto.efr.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class HouseMonitorDataStore : ViewModel(){
    val houseMonitorViList: MutableList<HouseMonitorVi> = mutableListOf()
    var houseMonitorIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var houseMonitorContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var houseMonitorView: HouseMonitorVi

}
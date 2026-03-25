package com.housemo.monisto

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.housemo.monisto.efr.HouseMonitorGlobalLayoutUtil
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication
import com.housemo.monisto.efr.presentation.pushhandler.HouseMonitorPushHandler
import com.housemo.monisto.efr.houseMonitorSetupSystemBars
import org.koin.android.ext.android.inject

class HouseMonitorActivity : AppCompatActivity() {

    private val houseMonitorPushHandler by inject<HouseMonitorPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        houseMonitorSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_house_monitor)

        val houseMonitorRootView = findViewById<View>(android.R.id.content)
        HouseMonitorGlobalLayoutUtil().houseMonitorAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(houseMonitorRootView) { houseMonitorView, houseMonitorInsets ->
            val houseMonitorSystemBars = houseMonitorInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val houseMonitorDisplayCutout = houseMonitorInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val houseMonitorIme = houseMonitorInsets.getInsets(WindowInsetsCompat.Type.ime())


            val houseMonitorTopPadding = maxOf(houseMonitorSystemBars.top, houseMonitorDisplayCutout.top)
            val houseMonitorLeftPadding = maxOf(houseMonitorSystemBars.left, houseMonitorDisplayCutout.left)
            val houseMonitorRightPadding = maxOf(houseMonitorSystemBars.right, houseMonitorDisplayCutout.right)
            window.setSoftInputMode(HouseMonitorApplication.houseMonitorInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "ADJUST PUN")
                val houseMonitorBottomInset = maxOf(houseMonitorSystemBars.bottom, houseMonitorDisplayCutout.bottom)

                houseMonitorView.setPadding(houseMonitorLeftPadding, houseMonitorTopPadding, houseMonitorRightPadding, 0)

                houseMonitorView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = houseMonitorBottomInset
                }
            } else {
                Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "ADJUST RESIZE")

                val houseMonitorBottomInset = maxOf(houseMonitorSystemBars.bottom, houseMonitorDisplayCutout.bottom, houseMonitorIme.bottom)

                houseMonitorView.setPadding(houseMonitorLeftPadding, houseMonitorTopPadding, houseMonitorRightPadding, 0)

                houseMonitorView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = houseMonitorBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Activity onCreate()")
        houseMonitorPushHandler.houseMonitorHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            houseMonitorSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        houseMonitorSetupSystemBars()
    }
}
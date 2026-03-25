package com.housemo.monisto.efr

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication

class HouseMonitorGlobalLayoutUtil {

    private var houseMonitorMChildOfContent: View? = null
    private var houseMonitorUsableHeightPrevious = 0

    fun houseMonitorAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        houseMonitorMChildOfContent = content.getChildAt(0)

        houseMonitorMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val houseMonitorUsableHeightNow = houseMonitorComputeUsableHeight()
        if (houseMonitorUsableHeightNow != houseMonitorUsableHeightPrevious) {
            val houseMonitorUsableHeightSansKeyboard = houseMonitorMChildOfContent?.rootView?.height ?: 0
            val houseMonitorHeightDifference = houseMonitorUsableHeightSansKeyboard - houseMonitorUsableHeightNow

            if (houseMonitorHeightDifference > (houseMonitorUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(HouseMonitorApplication.houseMonitorInputMode)
            } else {
                activity.window.setSoftInputMode(HouseMonitorApplication.houseMonitorInputMode)
            }
//            mChildOfContent?.requestLayout()
            houseMonitorUsableHeightPrevious = houseMonitorUsableHeightNow
        }
    }

    private fun houseMonitorComputeUsableHeight(): Int {
        val r = Rect()
        houseMonitorMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}
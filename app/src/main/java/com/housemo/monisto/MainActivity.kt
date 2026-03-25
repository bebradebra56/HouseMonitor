package com.housemo.monisto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.housemo.monisto.ui.navigation.AppNavigation
import com.housemo.monisto.ui.theme.HouseMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HouseMonitorTheme {
                AppNavigation()
            }
        }
    }
}

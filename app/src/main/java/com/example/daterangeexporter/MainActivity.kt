package com.example.daterangeexporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.daterangeexporter.DestinationsHandler.destinations
import com.example.daterangeexporter.core.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Destinations.CalendarExport,
                    builder = { destinations(navController) },
                )
            }
        }
    }
}

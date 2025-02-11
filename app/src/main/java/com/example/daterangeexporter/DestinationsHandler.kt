package com.example.daterangeexporter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.daterangeexporter.calendarExport.CalendarExportScreen
import com.example.daterangeexporter.calendars.CalendarsScreen
import java.util.Calendar

object DestinationsHandler {
    fun NavGraphBuilder.destinations(navController: NavController) {
        composable<Destinations.Calendars> {
            CalendarsScreen(
                onCalendarSelect = { month, year ->
                    navController.navigate(Destinations.CalendarExport)
                },
            )
        }
        composable<Destinations.CalendarExport> {
            CalendarExportScreen()
        }
    }
}

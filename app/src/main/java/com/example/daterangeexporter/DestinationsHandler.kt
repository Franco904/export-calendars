package com.example.daterangeexporter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.daterangeexporter.calendarExport.CalendarExportScreen
import com.example.daterangeexporter.calendars.CalendarsScreen

object DestinationsHandler {
    fun NavGraphBuilder.destinations(navController: NavController) {
        composable<Destinations.Calendars> {
            CalendarsScreen(
                onCalendarSelect = { month, year ->
                    navController.navigate(Destinations.CalendarExport(month, year))
                },
            )
        }
        composable<Destinations.CalendarExport> { backStackEntry ->
            val route = backStackEntry.toRoute<Destinations.CalendarExport>()

            val selectedMonth = route.month
            val selectedYear = route.year

            CalendarExportScreen(
                selectedMonth = selectedMonth,
                selectedYear = selectedYear,
                onUpNavigation = { navController.navigateUp() }
            )
        }
    }
}

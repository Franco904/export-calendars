package com.example.daterangeexporter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.daterangeexporter.calendarExport.CalendarExportScreen
import com.example.daterangeexporter.calendars.CalendarsScreen

object RouteHandler {
    fun NavGraphBuilder.routes(navController: NavController) {
        composable<Routes.Calendars> {
            CalendarsScreen(
                onCalendarClick = { timestamp ->
                    navController.navigate(Routes.CalendarExport(timestamp))
                },
            )
        }
        composable<Routes.CalendarExport> { backStackEntry ->
            val timestamp = backStackEntry.toRoute<Routes.CalendarExport>().timestamp

            CalendarExportScreen(
                timestamp = timestamp,
            )
        }
    }
}

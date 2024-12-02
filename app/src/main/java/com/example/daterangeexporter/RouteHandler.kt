package com.example.daterangeexporter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.daterangeexporter.calendars.CalendarsScreen

object RouteHandler {
    fun NavGraphBuilder.routes(navController: NavController) {
        composable<Routes.Calendars> {
            CalendarsScreen(
                onCalendarClick = { _ ->
                    navController.navigate(Routes.CalendarExport)
                },
            )
        }
        composable<Routes.CalendarExport> {}
    }
}

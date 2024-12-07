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
                onCalendarClick = { month, year ->
                    navController.navigate(Routes.CalendarExport(month, year))
                },
            )
        }
        composable<Routes.CalendarExport> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.CalendarExport>()

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

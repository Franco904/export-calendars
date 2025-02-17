package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.calendarExport.utils.CalendarExportUtilsImpl
import com.example.daterangeexporter.calendarExport.utils.interfaces.CalendarExportUtils
import org.koin.dsl.module
import java.util.Calendar
import java.util.TimeZone

val utilModule = module {
    single<CalendarExportUtils> {
        CalendarExportUtilsImpl(
            startDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")),
            endDateCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")),
        )
    }
}

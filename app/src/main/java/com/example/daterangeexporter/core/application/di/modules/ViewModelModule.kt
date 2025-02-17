package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.calendarExport.CalendarExportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.Calendar

val viewModelModule = module {
    viewModel {
        CalendarExportViewModel(
            calendar = Calendar.getInstance(),
            appContext = get(),
            calendarsRepository = get(),
            calendarExportUtils = get(),
        )
    }
}

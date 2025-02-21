package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.calendarExport.CalendarExportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.Calendar

val viewModelModule = module {
    viewModel {
        CalendarExportViewModel(
            calendar = Calendar.getInstance(),
            appContext = get(),
            calendarsRepository = get(),
            calendarsValidator = get(),
            calendarExportUtils = get(),
            appFileProviderHandler = get(),
        )
    }
}

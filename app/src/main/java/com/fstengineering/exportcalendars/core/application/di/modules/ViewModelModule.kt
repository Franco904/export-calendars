package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.calendarExport.CalendarExportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.Calendar

val viewModelModule = module {
    viewModel {
        CalendarExportViewModel(
            calendar = Calendar.getInstance(),
            appContext = get(),
            dataSourceErrorConverter = get(),
            validationErrorConverter = get(),
            calendarsRepository = get(),
            calendarsValidator = get(),
            calendarExportUtils = get(),
            appFileProviderHandler = get(),
            appLogger = get(),
        )
    }
}

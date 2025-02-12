package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.calendarExport.CalendarExportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        CalendarExportViewModel(
            appContext = get(),
            calendarsRepository = get(),
        )
    }
}

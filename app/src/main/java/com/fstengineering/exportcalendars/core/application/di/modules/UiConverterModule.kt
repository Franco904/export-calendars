package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.DataSourceErrorConverterImpl
import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.ValidationErrorConverterImpl
import org.koin.dsl.module

val uiConverterModule = module {
    single {
        DataSourceErrorConverterImpl()
    }

    single {
        ValidationErrorConverterImpl()
    }
}

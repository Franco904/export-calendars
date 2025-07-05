package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.domain.utils.DataSourceError
import com.fstengineering.exportcalendars.core.domain.utils.ValidationError
import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.DataSourceErrorConverterImpl
import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.ErrorConverter
import com.fstengineering.exportcalendars.core.presentation.utils.uiConverters.ValidationErrorConverterImpl
import org.koin.dsl.module

val uiConverterModule = module {
    single<ErrorConverter<DataSourceError>> {
        DataSourceErrorConverterImpl()
    }

    single<ErrorConverter<ValidationError>> {
        ValidationErrorConverterImpl()
    }
}

package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.core.domain.validators.CalendarsValidatorImpl
import com.fstengineering.daterangeexporter.core.domain.validators.interfaces.CalendarsValidator
import org.koin.dsl.module

val validatorModule = module {
    single<CalendarsValidator> {
        CalendarsValidatorImpl()
    }
}

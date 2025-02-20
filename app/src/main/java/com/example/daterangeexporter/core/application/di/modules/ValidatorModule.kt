package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.core.domain.validators.CalendarsValidatorImpl
import com.example.daterangeexporter.core.domain.validators.interfaces.CalendarsValidator
import org.koin.dsl.module

val validatorModule = module {
    single<CalendarsValidator> {
        CalendarsValidatorImpl()
    }
}

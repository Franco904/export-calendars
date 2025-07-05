package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.domain.validators.CalendarsValidatorImpl
import com.fstengineering.exportcalendars.core.domain.validators.interfaces.CalendarsValidator
import org.koin.dsl.module

val validatorModule = module {
    single<CalendarsValidator> {
        CalendarsValidatorImpl()
    }
}

package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.core.data.repositories.CalendarsRepositoryImpl
import com.fstengineering.daterangeexporter.core.domain.repositories.CalendarsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CalendarsRepository> {
        CalendarsRepositoryImpl(
            appSpecificStorage = get(),
            logger = get(),
        )
    }
}

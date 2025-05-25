package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.data.repositories.CalendarsRepositoryImpl
import com.fstengineering.exportcalendars.core.domain.repositories.CalendarsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CalendarsRepository> {
        CalendarsRepositoryImpl(
            appSpecificStorage = get(),
            storageStatsHandler = get(),
            logger = get(),
        )
    }
}

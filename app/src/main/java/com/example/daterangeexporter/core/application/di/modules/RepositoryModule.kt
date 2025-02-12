package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.core.data.repositories.CalendarsRepositoryImpl
import com.example.daterangeexporter.core.domain.repositories.CalendarsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CalendarsRepository> {
        CalendarsRepositoryImpl(
            internalStorage = get(),
        )
    }
}

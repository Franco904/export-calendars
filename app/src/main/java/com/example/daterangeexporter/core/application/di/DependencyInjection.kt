package com.example.daterangeexporter.core.application.di

import android.app.Application
import com.example.daterangeexporter.core.application.di.modules.dataSourceModule
import com.example.daterangeexporter.core.application.di.modules.monitoringModule
import com.example.daterangeexporter.core.application.di.modules.repositoryModule
import com.example.daterangeexporter.core.application.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)

        modules(
            dataSourceModule,
            repositoryModule,
            viewModelModule,
            monitoringModule,
        )
    }
}

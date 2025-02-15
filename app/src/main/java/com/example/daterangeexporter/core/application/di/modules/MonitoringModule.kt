package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.core.application.monitoring.AppLoggerImpl
import com.example.daterangeexporter.core.application.monitoring.interfaces.AppLogger
import org.koin.dsl.module

val monitoringModule = module {
    single<AppLogger> {
        AppLoggerImpl()
    }
}

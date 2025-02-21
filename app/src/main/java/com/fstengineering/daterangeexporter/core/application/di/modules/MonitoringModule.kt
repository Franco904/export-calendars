package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.core.application.monitoring.AppLoggerImpl
import com.fstengineering.daterangeexporter.core.application.monitoring.interfaces.AppLogger
import org.koin.dsl.module

val monitoringModule = module {
    single<AppLogger> {
        AppLoggerImpl()
    }
}

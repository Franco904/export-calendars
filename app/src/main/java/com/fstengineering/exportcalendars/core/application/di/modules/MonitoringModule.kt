package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.application.monitoring.AppLoggerImpl
import com.fstengineering.exportcalendars.core.application.monitoring.interfaces.AppLogger
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val monitoringModule = module {
    single<AppLogger> {
        AppLoggerImpl(
            analytics = Firebase.analytics,
        )
    }
}

package com.fstengineering.exportcalendars

import android.app.Application
import com.fstengineering.exportcalendars.core.application.di.configureDependencyInjection
import com.fstengineering.exportcalendars.core.application.monitoring.configureMonitoring

class ExportCalendarsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
        configureMonitoring()
    }
}

package com.fstengineering.daterangeexporter

import android.app.Application
import com.fstengineering.daterangeexporter.core.application.di.configureDependencyInjection

class CalendarExportApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
    }
}

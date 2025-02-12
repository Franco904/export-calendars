package com.example.daterangeexporter

import android.app.Application
import com.example.daterangeexporter.core.application.di.configureDependencyInjection

class CalendarExportApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        configureDependencyInjection()
    }
}

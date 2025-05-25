package com.fstengineering.exportcalendars.core.application.di

import android.app.Application
import com.fstengineering.exportcalendars.core.application.di.modules.contentProviderModule
import com.fstengineering.exportcalendars.core.application.di.modules.dataSourceModule
import com.fstengineering.exportcalendars.core.application.di.modules.monitoringModule
import com.fstengineering.exportcalendars.core.application.di.modules.repositoryModule
import com.fstengineering.exportcalendars.core.application.di.modules.utilModule
import com.fstengineering.exportcalendars.core.application.di.modules.validatorModule
import com.fstengineering.exportcalendars.core.application.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun Application.configureDependencyInjection() {
    startKoin {
        androidLogger()
        androidContext(this@configureDependencyInjection)

        modules(
            contentProviderModule,
            monitoringModule,
            utilModule,
            dataSourceModule,
            repositoryModule,
            validatorModule,
            viewModelModule,
        )
    }
}

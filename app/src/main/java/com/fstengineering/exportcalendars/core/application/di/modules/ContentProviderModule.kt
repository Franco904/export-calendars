package com.fstengineering.exportcalendars.core.application.di.modules

import com.fstengineering.exportcalendars.core.application.contentProviders.AppFileProviderHandlerImpl
import com.fstengineering.exportcalendars.core.application.contentProviders.interfaces.AppFileProviderHandler
import org.koin.dsl.module

val contentProviderModule = module {
    single<AppFileProviderHandler> {
        AppFileProviderHandlerImpl(
            appContext = get(),
        )
    }
}

package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.core.application.contentProviders.AppFileProviderHandlerImpl
import com.fstengineering.daterangeexporter.core.application.contentProviders.interfaces.AppFileProviderHandler
import org.koin.dsl.module

val contentProviderModule = module {
    single<AppFileProviderHandler> {
        AppFileProviderHandlerImpl(
            appContext = get(),
        )
    }
}

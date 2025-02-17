package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.core.application.contentProviders.AppFileProviderHandlerImpl
import com.example.daterangeexporter.core.application.contentProviders.interfaces.AppFileProviderHandler
import org.koin.dsl.module

val contentProviderModule = module {
    single<AppFileProviderHandler> {
        AppFileProviderHandlerImpl(
            appContext = get(),
        )
    }
}

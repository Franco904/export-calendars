package com.fstengineering.daterangeexporter.core.application.di.modules

import com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.AppSpecificStorageImpl
import com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import org.koin.core.module.Module
import org.koin.dsl.module

val dataSourceModule = module {
    configureInternalStorage()
}

fun Module.configureInternalStorage() {
    single<AppSpecificStorage> {
        AppSpecificStorageImpl(
            appContext = get(),
        )
    }
}

package com.example.daterangeexporter.core.application.di.modules

import com.example.daterangeexporter.core.data.dataSources.internalStorage.InternalStorageImpl
import com.example.daterangeexporter.core.data.dataSources.internalStorage.interfaces.InternalStorage
import org.koin.core.module.Module
import org.koin.dsl.module

val dataSourceModule = module {
    configureInternalStorage()
}

fun Module.configureInternalStorage() {
    single<InternalStorage> {
        InternalStorageImpl(
            appContext = get(),
        )
    }
}

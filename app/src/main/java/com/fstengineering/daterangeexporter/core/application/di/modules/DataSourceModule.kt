package com.fstengineering.daterangeexporter.core.application.di.modules

import android.app.usage.StorageStatsManager
import android.content.Context
import androidx.core.content.getSystemService
import com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.AppSpecificStorageImpl
import com.fstengineering.daterangeexporter.core.data.dataSources.appSpecificStorage.interfaces.AppSpecificStorage
import com.fstengineering.daterangeexporter.core.data.dataSources.storageStats.StorageStatsHandlerImpl
import com.fstengineering.daterangeexporter.core.data.dataSources.storageStats.interfaces.StorageStatsHandler
import org.koin.core.module.Module
import org.koin.dsl.module

val dataSourceModule = module {
    configureAppSpecificStorage()
    configureStorageStats()
}

fun Module.configureAppSpecificStorage() {
    single<AppSpecificStorage> {
        AppSpecificStorageImpl(
            appContext = get(),
        )
    }
}

fun Module.configureStorageStats() {
    single<StorageStatsHandler> {
        val appContext: Context = get()

        StorageStatsHandlerImpl(
            storageStatsManager = appContext.getSystemService<StorageStatsManager>()!!,
        )
    }
}

package com.fstengineering.daterangeexporter.core.data.dataSources.storageStats

import android.app.usage.StorageStatsManager
import android.os.storage.StorageManager
import com.fstengineering.daterangeexporter.core.data.dataSources.storageStats.interfaces.StorageStatsHandler

class StorageStatsHandlerImpl(
    private val storageStatsManager: StorageStatsManager,
) : StorageStatsHandler {
    override fun getDeviceFreeStorageBytes(): Long {
        return storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
    }

    override fun getDeviceTotalStorageBytes(): Long {
        return storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
    }
}

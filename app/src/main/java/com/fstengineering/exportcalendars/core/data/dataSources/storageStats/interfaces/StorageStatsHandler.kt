package com.fstengineering.exportcalendars.core.data.dataSources.storageStats.interfaces

interface StorageStatsHandler {
    fun getDeviceFreeStorageBytes(): Long

    fun getDeviceTotalStorageBytes(): Long
}

package com.fstengineering.daterangeexporter.core.data.dataSources.storageStats.interfaces

interface StorageStatsHandler {
    fun getDeviceFreeStorageBytes(): Long

    fun getDeviceTotalStorageBytes(): Long
}

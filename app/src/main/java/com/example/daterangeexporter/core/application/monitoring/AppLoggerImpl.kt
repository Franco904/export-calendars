package com.example.daterangeexporter.core.application.monitoring

import android.util.Log
import com.example.daterangeexporter.core.application.monitoring.interfaces.AppLogger

class AppLoggerImpl : AppLogger {
    override fun logError(tag: String, message: String) {
        Log.e(tag, message)
    }
}

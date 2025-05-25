package com.fstengineering.exportcalendars.core.application.monitoring

import android.os.Bundle
import android.util.Log
import com.fstengineering.exportcalendars.core.application.monitoring.interfaces.AppLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

class AppLoggerImpl(
    private val analytics: FirebaseAnalytics,
) : AppLogger {
    override fun logEvent(name: String) {
        analytics.logEvent(name, Bundle())
    }

    override fun logError(tag: String, message: String) {
        Log.e(tag, message)
    }
}

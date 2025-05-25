package com.fstengineering.exportcalendars.core.application.monitoring

import com.fstengineering.exportcalendars.BuildConfig
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

fun configureMonitoring() {
    if (BuildConfig.BUILD_TYPE == "debug" || BuildConfig.FLAVOR == "dev") {
        // Use dev-specific logic
        Firebase.analytics.setUserProperty("env", "dev")
    } else {
        Firebase.analytics.setUserProperty("env", "prod")
    }
}

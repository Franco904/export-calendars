package com.fstengineering.exportcalendars.core.presentation.utils

import android.content.Context
import android.content.Intent

fun Context.showShareSheet(
    action: String,
    intentConfig: Intent.() -> Unit,
) {
    val sendIntent = Intent(action).apply { intentConfig() }
    val shareSheet = Intent.createChooser(sendIntent, null, null)

    startActivity(shareSheet)
}

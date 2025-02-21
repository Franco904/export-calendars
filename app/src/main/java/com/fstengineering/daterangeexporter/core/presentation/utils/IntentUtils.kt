package com.fstengineering.daterangeexporter.core.presentation.utils

import android.content.Context
import android.content.Intent

const val IMAGE_PNG_TYPE = "image/png"

fun Context.showShareSheet(
    action: String,
    intentConfig: Intent.() -> Unit,
) {
    val sendIntent = Intent(action).apply { intentConfig() }
    val shareSheet = Intent.createChooser(sendIntent, null, null)

    startActivity(shareSheet)
}

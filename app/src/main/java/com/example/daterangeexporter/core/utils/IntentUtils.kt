package com.example.daterangeexporter.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

const val IMAGE_PNG_TYPE = "image/png"

fun Context.showShareSheet(
    action: String,
    intentConfig: Intent.() -> Unit,
) {
    val sendIntent = Intent(action).apply { intentConfig() }
    val shareSheet = Intent.createChooser(sendIntent, null, null)

    startActivity(shareSheet)
}

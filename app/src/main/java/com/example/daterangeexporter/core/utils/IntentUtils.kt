package com.example.daterangeexporter.core.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle

const val IMAGE_PNG_TYPE = "image/png"

fun Context.showShareSheet(
    intentType: String,
    intentFlags: Int,
    extras: Bundle,
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = intentType
        flags = intentFlags

        putExtras(extras)
    }

    val shareSheet = Intent.createChooser(sendIntent, null, null)
    startActivity(shareSheet)
}

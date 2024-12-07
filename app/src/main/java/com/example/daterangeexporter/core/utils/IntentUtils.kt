package com.example.daterangeexporter.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

const val IMAGE_PNG_TYPE = "image/png"

fun Context.showShareSheet(
    action: String,
    intentData: Uri? = null,
    intentType: String,
    intentFlags: Int,
    extras: Bundle,
) {
    val sendIntent = Intent(action).apply {
        setDataAndType(intentData, intentType)
        flags = intentFlags

        putExtras(extras)
    }

    val shareSheet = Intent.createChooser(sendIntent, null, null)
    startActivity(shareSheet)
}

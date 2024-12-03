package com.example.daterangeexporter.calendarExport

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.daterangeexporter.calendars.CalendarsScreen
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

private const val IMAGE_PNG_TYPE = "image/png"

@Composable
fun CalendarExportScreen(
    timestamp: Long = 0L,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Scaffold(
        modifier = modifier
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(graphicsLayer)
            }
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = Color.Cyan,
                        shape = MaterialTheme.shapes.small,
                    )
            )
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = Color.LightGray,
                        shape = MaterialTheme.shapes.small,
                    )
                    .clickable {
                        coroutineScope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            context.shareImage(bitmap.asAndroidBitmap())
                        }
                    }
            )
        }
    }
}

private fun Context.shareImage(bitmap: Bitmap) {
    val currentTimestamp = Calendar.getInstance().timeInMillis
    val file = saveToDisk(bitmap, fileName = "calendar-$currentTimestamp.png")

    val contentUri = FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = IMAGE_PNG_TYPE
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        putExtra(Intent.EXTRA_STREAM, contentUri)
    }

    startActivity(Intent.createChooser(shareIntent, null))
}

private fun Context.saveToDisk(bitmap: Bitmap, fileName: String): File {
    deleteCalendarFiles()

    val file = File(filesDir, fileName)
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }

    return file
}

private fun Context.deleteCalendarFiles() {
    filesDir.listFiles()?.forEach { file ->
        if (file.isFile && file.name.startsWith("calendar-")) {
            file.delete()
        }
    }
}

@Preview
@Composable
fun CalendarsScreenPreview(
    modifier: Modifier = Modifier,
) {
    CalendarsScreen(modifier = modifier)
}

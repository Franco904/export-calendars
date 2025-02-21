package com.fstengineering.daterangeexporter.core.application.contentProviders

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.fstengineering.daterangeexporter.core.application.contentProviders.interfaces.AppFileProviderHandler
import java.io.File

class AppFileProviderHandlerImpl(
    private val appContext: Context,
) : AppFileProviderHandler {
    override fun getUriForInternalAppFile(file: File): Uri {
        return FileProvider.getUriForFile(
            /* context = */ appContext,
            /* authority = */ "${appContext.packageName}.fileprovider",
            file,
        )
    }
}

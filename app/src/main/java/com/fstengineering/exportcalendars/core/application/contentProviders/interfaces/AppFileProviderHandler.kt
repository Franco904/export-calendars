package com.fstengineering.exportcalendars.core.application.contentProviders.interfaces

import android.net.Uri
import java.io.File

interface AppFileProviderHandler {
    fun getUriForInternalAppFile(file: File): Uri
}

package com.fstengineering.exportcalendars.data.dataSources.appSpecificStorage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.fstengineering.exportcalendars.core.data.dataSources.appSpecificStorage.AppSpecificStorageImpl
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import java.io.File

@SmallTest
class AppSpecificStorageImplTest {
    private lateinit var sut: AppSpecificStorageImpl

    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        sut = AppSpecificStorageImpl(
            appContext = appContext,
        )
    }

    @Test
    fun shouldInsertAPngFileForTheProvidedImageBitmap(): Unit = runBlocking {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val fileName = "test-bitmap.png"
        val parentFolder = appContext.cacheDir

        val bitmapFile = sut.saveImage(
            bitmap = bitmap,
            fileName = fileName,
            parentFolder = parentFolder,
        )

        bitmapFile.exists() shouldBeEqualTo true

        val savedBitmap = BitmapFactory.decodeFile(File(parentFolder, fileName).absolutePath)
        savedBitmap.sameAs(bitmap) shouldBeEqualTo true
    }

    @Test
    fun shouldClearCachesDirectory(): Unit = runBlocking {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val fileName = "test-bitmap.png"
        val parentFolder = appContext.cacheDir

        val bitmapFile = sut.saveImage(
            bitmap = bitmap,
            fileName = fileName,
            parentFolder = parentFolder,
        )

        sut.clearCacheDir()

        bitmapFile.exists() shouldBeEqualTo false
    }
}

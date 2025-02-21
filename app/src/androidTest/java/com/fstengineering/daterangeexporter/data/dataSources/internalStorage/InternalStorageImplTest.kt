package com.fstengineering.daterangeexporter.data.dataSources.internalStorage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.fstengineering.daterangeexporter.core.data.dataSources.internalStorage.InternalStorageImpl
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import java.io.File

class InternalStorageImplTest {
    private lateinit var sut: InternalStorageImpl

    private lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        sut = InternalStorageImpl(
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

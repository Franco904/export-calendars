package com.example.daterangeexporter.core.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.platform.ComposeView
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.example.daterangeexporter.R
import com.example.daterangeexporter.core.composables.BaseCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class BaseCalendarState(
    val month: Int,
    val year: Int,
    val selectedDates: List<String> = emptyList(),
    val hasTheStartDate: Boolean = false,
    val hasTheEndDate: Boolean = false,
)

object ComposableToBitmapConverter {
    suspend fun Context.captureCalendarComposablesAsBitmap(
        calendarStates: List<BaseCalendarState>,
    ): List<Bitmap> {
        val context = this@captureCalendarComposablesAsBitmap

//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val layoutInflater = LayoutInflater.from(this)
//
////        val frameLayout = FrameLayout(context)
//        val view = layoutInflater.inflate(R.layout.calendar_parent_view, null, false) as ViewGroup
//
//        val layoutParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
//            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,
//            PixelFormat.TRANSLUCENT
//        )
//
//        layoutParams.dimAmount = 0.5f
//        windowManager.addView(view, layoutParams)

        val bitmaps = calendarStates.map { calendarState ->
            val composeView = ComposeView(context).apply {
                setContent {
                    BaseCalendar(
                        month = calendarState.month,
                        year = calendarState.year,
                        hasTheStartDate = calendarState.hasTheStartDate,
                        hasTheEndDate = calendarState.hasTheEndDate,
                        selectedDates = calendarState.selectedDates,
                        isCardSelected = false,
                    )
                }

                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                layout(0, 0, measuredWidth, measuredHeight)
            }

            val bitmap = composeView.toBitmap()
            composeView.draw(Canvas(bitmap))

            bitmap
        }
//        view.removeAllViews()
//        windowManager.removeView(view)

        return bitmaps
    }

    private suspend fun View.toBitmap(): Bitmap = withContext(Dispatchers.Default) {
        Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    }
}
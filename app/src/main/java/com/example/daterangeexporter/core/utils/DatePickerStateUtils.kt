package com.example.daterangeexporter.core.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import com.example.daterangeexporter.calendarExport.localModels.CalendarMonthYear
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
fun getSelectableDates(
    initialCalendar: CalendarMonthYear,
) = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val initialMonthFirstDay = Calendar.getInstance().apply {
            set(Calendar.MONTH, initialCalendar.month - 1)
            set(Calendar.YEAR, initialCalendar.year)
            set(Calendar.DAY_OF_MONTH, 0)
        }

        return utcTimeMillis >= initialMonthFirstDay.timeInMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= initialCalendar.year
    }
}

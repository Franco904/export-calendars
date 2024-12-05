package com.example.daterangeexporter.core

import android.icu.util.Calendar

val months = mapOf(
    1 to "JANEIRO",
    2 to "FEVEREIRO",
    3 to "MARÇO",
    4 to "ABRIL",
    5 to "MAIO",
    6 to "JUNHO",
    7 to "JULHO",
    8 to "AGOSTO",
    9 to "SETEMBRO",
    10 to "OUTUBRO",
    11 to "NOVEMBRO",
    12 to "DEZEMBRO",
)

fun getMonthLabelByNumber(monthNumber: Int) = months[monthNumber] ?: months[1]

fun getNumberOfDaysOfMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)

    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getFirstDayOfWeekOfMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)

    return calendar.get(Calendar.DAY_OF_WEEK)
}

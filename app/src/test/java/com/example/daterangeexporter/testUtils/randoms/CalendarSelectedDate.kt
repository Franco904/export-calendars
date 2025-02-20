package com.example.daterangeexporter.testUtils.randoms

import com.example.daterangeexporter.calendarExport.models.CalendarSelectedDate
import com.example.daterangeexporter.calendarExport.models.RangeSelectionLabel
import com.example.daterangeexporter.testUtils.faker

fun createCalendarSelectedDateRandom() = CalendarSelectedDate(
    dayOfMonth = faker.random.nextInt(min = 1, max = 31).toString(),
    isRangeStart = faker.random.nextBoolean(),
    isRangeEnd = faker.random.nextBoolean(),
    rangeSelectionLabel = Pair(
        RangeSelectionLabel.entries.random(),
        RangeSelectionLabel.entries.random(),
    ),
)

package com.fstengineering.exportcalendars.testUtils.randoms

import com.fstengineering.exportcalendars.calendarExport.models.CalendarSelectedDate
import com.fstengineering.exportcalendars.calendarExport.models.RangeSelectionLabel
import com.fstengineering.exportcalendars.testUtils.faker

fun createCalendarSelectedDateRandom() = CalendarSelectedDate(
    dayOfMonth = faker.random.nextInt(min = 1, max = 31).toString(),
    isRangeStart = faker.random.nextBoolean(),
    isRangeEnd = faker.random.nextBoolean(),
    rangeSelectionLabel = Pair(
        RangeSelectionLabel.entries.random(),
        RangeSelectionLabel.entries.random(),
    ),
)

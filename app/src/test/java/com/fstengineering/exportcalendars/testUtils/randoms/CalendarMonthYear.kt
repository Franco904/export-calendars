package com.fstengineering.exportcalendars.testUtils.randoms

import com.fstengineering.exportcalendars.calendarExport.models.CalendarMonthYear
import com.fstengineering.exportcalendars.testUtils.faker

fun createCalendarMonthYearRandom() = CalendarMonthYear(
    id = faker.random.nextInt(),
    month = faker.random.nextInt(),
    year = faker.random.nextInt(),
)

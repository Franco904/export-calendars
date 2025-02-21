package com.fstengineering.daterangeexporter.testUtils.randoms

import com.fstengineering.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.fstengineering.daterangeexporter.testUtils.faker

fun createCalendarMonthYearRandom() = CalendarMonthYear(
    id = faker.random.nextInt(),
    month = faker.random.nextInt(),
    year = faker.random.nextInt(),
)

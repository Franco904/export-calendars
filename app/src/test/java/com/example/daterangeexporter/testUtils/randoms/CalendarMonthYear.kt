package com.example.daterangeexporter.testUtils.randoms

import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.testUtils.faker

fun createCalendarMonthYearRandom() = CalendarMonthYear(
    id = faker.random.nextInt(),
    month = faker.random.nextInt(),
    year = faker.random.nextInt(),
)

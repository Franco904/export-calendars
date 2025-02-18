package com.example.daterangeexporter.testUtils.fakes

import com.example.daterangeexporter.calendarExport.models.CalendarMonthYear
import com.example.daterangeexporter.testUtils.faker

fun createCalendarMonthYearFake() = CalendarMonthYear(
    id = faker.random.nextInt(),
    month = faker.random.nextInt(),
    year = faker.random.nextInt(),
)

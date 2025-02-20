package com.example.daterangeexporter.core.domain.utils

sealed interface ValidationError : Error {
    enum class CalendarLabel : ValidationError {
        IsBlank,
        LengthIsGreaterThan25Chars,
    }
}

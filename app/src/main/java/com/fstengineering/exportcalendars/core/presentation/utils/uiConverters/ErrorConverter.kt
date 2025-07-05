package com.fstengineering.exportcalendars.core.presentation.utils.uiConverters

interface ErrorConverter<T> {
    fun toUiMessage(error: T): Int
}

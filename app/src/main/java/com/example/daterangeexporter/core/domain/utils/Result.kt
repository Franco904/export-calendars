package com.example.daterangeexporter.core.domain.utils

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

inline fun <D, E : RootError> Result<D, E>.onError(
    block: (E) -> Unit,
) = apply {
    if (this is Result.Error<D, E>) {
        block(error)
    }
}

inline fun <D, E : RootError> Result<D, E>.onSuccess(
    block: (D) -> Unit,
) = apply {
    if (this is Result.Success<D, E>) {
        block(data)
    }
}

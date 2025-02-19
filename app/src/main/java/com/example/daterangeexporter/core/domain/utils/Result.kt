package com.example.daterangeexporter.core.domain.utils

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

inline fun <D, E : RootError> Result<D, E>.onError(
    onError: (E) -> Unit,
) = apply {
    if (this is Result.Error<D, E>) {
        onError(error)
    }
}

inline fun <D, E : RootError> Result<D, E>.onSuccess(
    onSuccess: (D) -> Unit,
) = apply {
    if (this is Result.Success<D, E>) {
        onSuccess(data)
    }
}

inline fun <D, E : RootError, R> Result<D, E>.fold(
    onError: (error: E) -> R,
    onSuccess: (data: D) -> R,
): R {
    return when (this) {
        is Result.Error<D, E> -> onError(error)
        is Result.Success<D, E> -> onSuccess(data)
    }
}

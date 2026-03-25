package itmo.alk.womplist.core.error

import java.io.IOException

sealed interface AppError {
    data class Network(val details: String? = null) : AppError
    data class Unknown(val details: String? = null) : AppError
}

fun Throwable.toAppError(): AppError {
    return when (this) {
        is IOException -> AppError.Network(message)
        else -> AppError.Unknown(message)
    }
}

fun AppError.toUiMessage(): String {
    return when (this) {
        is AppError.Network -> details ?: "Network error"
        is AppError.Unknown -> details ?: "Unexpected error"
    }
}


package com.example.simplemusic.models

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class RepositoryResult<out R> {

    data class Success<out T>(val data: T) : RepositoryResult<T>()
    data class Error(val exception: Exception) : RepositoryResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val RepositoryResult<*>.succeeded
    get() = this is RepositoryResult.Success && data != null

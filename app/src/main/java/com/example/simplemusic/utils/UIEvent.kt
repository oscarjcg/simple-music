package com.example.simplemusic.utils

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class UIEvent<out R> {

    object CheckInternet : UIEvent<Nothing>()
    object EmptyList: UIEvent<Nothing>()

    override fun toString(): String {
        return when (this) {
            is CheckInternet -> "CheckInternet"
            is EmptyList -> "EmptyList"
        }
    }
}

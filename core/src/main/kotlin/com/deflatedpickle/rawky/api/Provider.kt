package com.deflatedpickle.rawky.api

interface Provider<out T> {
    fun provide(
        row: Int, column: Int,
    ): T
}
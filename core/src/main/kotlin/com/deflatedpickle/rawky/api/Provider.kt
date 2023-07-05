/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api

interface Provider<out T> {
    fun provide(
        row: Int,
        column: Int,
    ): T
}

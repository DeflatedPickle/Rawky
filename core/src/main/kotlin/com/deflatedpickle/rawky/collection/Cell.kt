/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.collection

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.awt.Color
import java.awt.Rectangle

@Serializable
abstract class Cell<out T> {
    abstract val row: Int
    abstract val column: Int
    @Polymorphic abstract var content: @UnsafeVariance T

    @Transient lateinit var polygon: Rectangle
    @Transient lateinit var grid: Grid

    fun <S : @UnsafeVariance T> set(value: S) {
        content = value
    }

    operator fun invoke(func: Cell<T>.() -> Unit) = this.apply(func)

    companion object {
        val defaultColour = Color(0, 0, 0, 0)
    }
}

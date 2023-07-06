/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api

import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.collection.Cell
import kotlinx.serialization.Contextual
import java.awt.Graphics2D

abstract class CellProvider<T> : Provider<Cell<T>>, HasName {
    companion object : HasRegistry<String, CellProvider<out Any>>, HasCurrent<CellProvider<out Any>> {
        override val registry = Registry<String, CellProvider<out Any>>()
        override lateinit var current: CellProvider<out Any>
    }

    abstract var current: @UnsafeVariance T
    abstract var default: @UnsafeVariance T

    fun current(value: Any) {
        current = value as T
    }

    abstract fun perform(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    abstract fun redact(
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    abstract fun cleanup(
        cache: Any,
        cell: Cell<Any>,
        button: Int,
        dragged: Boolean,
        clickCount: Int,
    )

    abstract fun paintGrid(
        g: Graphics2D,
        cell: Cell<@Contextual Any>
    )

    abstract fun paintHover(
        g: Graphics2D,
        cell: Cell<Any>
    )

    override fun toString() = name
}

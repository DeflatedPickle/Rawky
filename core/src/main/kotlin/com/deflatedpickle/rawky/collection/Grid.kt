/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.Matrix
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.awt.Rectangle

@Serializable
data class Grid(
    override val rows: Int = 0,
    override val columns: Int = 0,
    override var selectedIndex: Int = 0,
    override val children: MutableList<@Polymorphic Cell<@Contextual Any>> = mutableListOf(),
) : Matrix<@Polymorphic Cell<@Contextual Any>>, ChildSelector {
    companion object {
        const val pixel = 16
    }

    @Transient lateinit var layer: Layer

    init {
        val width = pixel
        val height = pixel
        var column = 0
        var row = 0

        this.children.addAll(
            Array(this.columns * this.rows) {
                val cell = CellProvider.current.provide(column, row).apply {
                    grid = this@Grid
                    polygon = Rectangle(column * pixel, row * pixel, width, height)
                }

                if (row + 1 == this.columns) {
                    if (column + 1 == this.rows) column = 0 else column++

                    row = 0
                } else {
                    row++
                }

                cell
            },
        )
    }

    operator fun get(column: Int, row: Int) = children[(column * columns) + row]

    operator fun set(column: Int, row: Int, value: Cell<Any>) =
        children.set((column * columns) + row, value)
}

package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.Matrix
import kotlinx.serialization.Serializable
import java.awt.Rectangle

@Serializable
class Grid(
    override val rows: Int,
    override val columns: Int,
    override var selectedIndex: Int = 0
) : Matrix<Cell>, ChildSelector {
    override val children: Array<Cell>

    init {
        val width = 16
        val height = 16
        var row = -1
        var column = -1

        this.children = Array(this.rows * this.columns) {
            if (row + 1 == this.rows) {
                if (column + 1 == this.columns) column = 0
                else column++

                row = 0
            } else {
                row++
            }

            Cell(
                row = row,
                column = column,
                polygon = Rectangle(
                    row * 16, column * 16,
                    width, height
                )
            )
        }
    }

    operator fun get(row: Int, column: Int) = children[(column * columns) + row]
    operator fun set(row: Int, column: Int, value: Cell) = children.set((column * columns) + row, value)
}
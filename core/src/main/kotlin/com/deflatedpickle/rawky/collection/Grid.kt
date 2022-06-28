package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.Matrix
import kotlinx.serialization.Serializable
import java.awt.Rectangle

@Serializable
data class Grid(
    override val rows: Int = 0,
    override val columns: Int = 0,
    override var selectedIndex: Int = 0,
    override val children: MutableList<Cell> = mutableListOf()
) : Matrix<Cell>, ChildSelector {
    init {
        val width = 16
        val height = 16
        var row = 0
        var column = 0

        this.children.addAll(
            Array(this.rows * this.columns) {
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
        )
    }

    operator fun get(row: Int, column: Int) = children[(column * columns) + row]
    operator fun set(row: Int, column: Int, value: Cell) = children.set((column * columns) + row, value)
}
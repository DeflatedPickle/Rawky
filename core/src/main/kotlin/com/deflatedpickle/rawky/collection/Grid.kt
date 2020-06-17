package com.deflatedpickle.rawky.collection

import com.deflatedpickle.rawky.api.relation.ChildSelector
import com.deflatedpickle.rawky.api.relation.Matrix

class Grid(
    override val rows: Int,
    override val columns: Int,
    override var parent: Layer,
    override val children: MutableList<Cell>,
    override var currentChild: Cell
) : Matrix<Layer, Cell>, ChildSelector<Cell>
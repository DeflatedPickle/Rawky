/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api

import com.deflatedpickle.rawky.collection.Cell
import java.awt.Graphics2D

interface Painter<out T> {
    fun paint(
        hoverCell: Cell<out @UnsafeVariance T>,
        graphics: Graphics2D,
    )
}

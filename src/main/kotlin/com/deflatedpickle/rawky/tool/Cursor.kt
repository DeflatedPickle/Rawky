/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.Icons
import java.awt.Point

class Cursor : Tool("Cursor", listOf(Icons.arrow), group = Toolbox.Group.SECONDARY) {
    override fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        if (PixelGrid.hoverPixel != null) {
            PixelGrid.lastCell.setLocation(
                    PixelGrid.hoverPixel!!.bounds.x,
                    PixelGrid.hoverPixel!!.bounds.y
            )
        }

        PixelGrid.contextMenu.show(PixelGrid, PixelGrid.lastCell.x, PixelGrid.lastCell.y)
    }
}

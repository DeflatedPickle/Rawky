package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.Point

class Cursor : Tool("Cursor", listOf(Icons.arrow), Icons.arrow.image, false) {
    override fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {
        if (Components.pixelGrid.hoverPixel != null) {
            Components.pixelGrid.lastCell.setLocation(Components.pixelGrid.hoverPixel!!.x, Components.pixelGrid.hoverPixel!!.y)
        }

        Components.pixelGrid.contextMenu.show(Components.pixelGrid, Components.pixelGrid.lastCell.x, Components.pixelGrid.lastCell.y)
    }
}
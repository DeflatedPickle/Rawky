package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.util.DocumentUtil
import java.awt.Color
import java.awt.Graphics

@RedrawActive
object PixelGridComponent : PluginPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val doc = DocumentUtil.document
        if (doc != null) {
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            val grid = layer.child

            this.paintGrid(g, grid)
        }
    }

    private fun paintGrid(g: Graphics, grid: Grid) {
        for (cell in grid.children) {
            g.color = cell.colour
            g.fillRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )

            g.color = Color.BLACK
            g.drawRect(
                cell.polygon.x, cell.polygon.y,
                cell.polygon.width, cell.polygon.height
            )
        }
    }
}
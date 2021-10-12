package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.Core
import com.deflatedpickle.rawky.collection.Grid
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import javax.swing.JTextField

@RedrawActive
object PixelGridPanel : PluginPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        Core.document?.let { doc ->
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
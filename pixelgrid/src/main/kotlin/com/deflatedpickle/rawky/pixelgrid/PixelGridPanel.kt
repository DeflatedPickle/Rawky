@file:Suppress("USELESS_ELVIS")

package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.RawkySettings
import com.deflatedpickle.rawky.api.Painter
import com.deflatedpickle.rawky.api.Tool
import com.deflatedpickle.rawky.collection.Cell
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.event.EventUpdateCell
import com.deflatedpickle.rawky.pixelgrid.setting.PixelGridSettings
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.DrawUtil
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MouseInfo
import java.awt.Stroke
import javax.swing.SwingUtilities

@RedrawActive
object PixelGridPanel : PluginPanel() {
    val selectedCells = mutableListOf<Cell>()

    init {
        EventUpdateCell.addListener {
            repaint()
        }
    }

    fun paint(
        button: Int,
        dragged: Boolean,
        count: Int,
        cells: List<Cell> = selectedCells,
        tool: Tool = Tool.current,
    ) {
        for (cell in cells) {
            tool.perform(
                cell,
                button,
                dragged,
                count,
            )

            EventUpdateCell.trigger(cell)
        }
    }

    private fun drawCursor(g: Graphics) {
        val settings = ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")
        val width = settings?.cursorSize?.x ?: Tool.defaultSize
        val height = settings?.cursorSize?.y ?: Tool.defaultSize

        if (Tool.isToolValid() && MouseInfo.getPointerInfo() != null) {
            val point = MouseInfo.getPointerInfo().location
            SwingUtilities.convertPointFromScreen(point, this)

            g.drawImage(
                Tool.current.icon.image.getScaledInstance(
                    width,
                    height,
                    Image.SCALE_AREA_AVERAGING
                ),
                point.x,
                point.y,
                null,
            )
        }
    }

    private fun drawGuides(g: Graphics2D, doc: RawkyDocument, colour: Color, stroke: Stroke) {
        g.color = colour
        g.stroke = stroke

        for (i in doc.guides) {
            g.drawString(i.name, i.x * Grid.pixel + 4, i.y * Grid.pixel + g.fontMetrics.height + 4)
            g.drawRect(i.x * Grid.pixel, i.y * Grid.pixel, i.width * Grid.pixel, i.height * Grid.pixel)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]
            val grid = layer.child

            val settings = ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")

            settings?.let { settings ->
                val g2d = g as Graphics2D

                DrawUtil.paintGrid(g, grid, settings.divide.colour, BasicStroke(settings.divide.thickness))
                drawGuides(g, doc, settings.guide.colour, BasicStroke(settings.guide.thickness))
                DrawUtil.paintHoverCell(selectedCells, g)

                val tool = Tool.current
                if (selectedCells.size > 0 && tool is Painter) {
                    tool.paint(selectedCells.first(), g)
                }
            }
        }

        drawCursor(g)
    }
}
package com.deflatedpickle.rawky.pixelgrid

import com.deflatedpickle.haruhi.api.redraw.RedrawActive
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.setting.RawkySettings
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
            if (cell.grid.layer.lock) continue

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

    private fun drawDebug(g: Graphics2D, doc: RawkyDocument) {
        val frame = doc.children[doc.selectedIndex]
        val layer = frame.children[frame.selectedIndex]
        val grid = layer.child

        ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")?.let {
            g.color = it.debug.colour
            g.font = it.debug.font
        }

        g.drawString("grid: ${grid.rows}x${grid.columns}", 5, g.fontMetrics.height + 5)
        g.drawString("frame: ${doc.selectedIndex}", 5, g.fontMetrics.height * 2 + 5)
        g.drawString("layer: ${frame.selectedIndex}", 5, g.fontMetrics.height * 3 + 5)

        g.drawString("visible: ${layer.visible}", 5, g.fontMetrics.height * 5 + 5)
        g.drawString("lock: ${layer.lock}", 5, g.fontMetrics.height * 6 + 5)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D

        RawkyPlugin.document?.let { doc ->
            val frame = doc.children[doc.selectedIndex]

            for (layer in frame.children) {
                val grid = layer.child

                ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")?.let { settings ->
                    if (layer.visible) {
                        DrawUtil.paintGridFill(g2d, grid, settings.divide.colour)
                    }

                    DrawUtil.paintGridOutline(g2d, grid, settings.divide.colour, BasicStroke(settings.divide.thickness))
                }
            }

            ConfigUtil.getSettings<PixelGridSettings>("deflatedpickle@pixel_grid#*")?.let { settings ->
                drawGuides(g2d, doc, settings.guide.colour, BasicStroke(settings.guide.thickness))
                DrawUtil.paintHoverCell(selectedCells, g2d)

                if (Tool.isToolValid()) {
                    val tool = Tool.current
                    if (selectedCells.size > 0 && tool is Painter) {
                        tool.paint(selectedCells.first(), g2d)
                    }
                }
            }

            ConfigUtil.getSettings<RawkySettings>("deflatedpickle@core#*")?.let {
                if (it.debug.enabled) {
                    drawDebug(g2d, doc)
                }
            }
        }

        drawCursor(g)
    }
}
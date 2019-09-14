package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.util.*
import javax.swing.JPanel
import javax.swing.SwingUtilities

class PixelGrid : JPanel() {
    class Frame {
        var layerList = mutableListOf<Layer>()

        override fun toString(): String {
            return "Frame { $layerList }"
        }
    }

    class Layer {
        var pixelMatrix: MutableList<MutableList<Cell>>
        var visible = true
        var locked = false

        init {
            val rowList = mutableListOf<MutableList<Cell>>()
            for (row in 0 until Components.pixelGrid.rowAmount) {
                val columnList = mutableListOf<Cell>()
                for (column in 0 until Components.pixelGrid.columnAmount) {
                    columnList.add(Cell())
                }
                rowList.add(columnList)
            }
            pixelMatrix = rowList
        }

        override fun toString(): String {
            return "Layer { $pixelMatrix, $visible, $locked }"
        }
    }

    class Cell {
        var colour: Color? = null

        override fun toString(): String {
            return "Cell { $colour }"
        }
    }

    var pixelSize = 20
    var pixelSmooth = 0

    var backgroundPixelSize = pixelSize / 3
    var backgroundFillEven = Color.LIGHT_GRAY
    var backgroundFillOdd = Color.WHITE

    var hoverOpacity = 225 / 3

    var rowAmount = 16
    var columnAmount = 16

    var lineThickness = 1f
    var gridColour = Color.GRAY

    var rectangleMatrix: MutableList<MutableList<Rectangle>>
    var frameList = mutableListOf<Frame>()

    var hoverPixel: Rectangle? = null
    var hoverRow = 0
    var hoverColumn = 0

    init {
        isOpaque = false

        rectangleMatrix = refreshMatrix()

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                for ((rowIndex, row) in rectangleMatrix.withIndex()) {
                    for ((columnIndex, column) in row.withIndex()) {
                        if (column.contains(e.point)) {
                            hoverPixel = column
                            hoverRow = rowIndex
                            hoverColumn = columnIndex
                        }
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                mouseMoved(e)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.tool.performLeft()
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool.performMiddle()
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.tool.performRight()
                }
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.tool.performLeft()
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool.performMiddle()
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.tool.performRight()
                }
            }

            override fun mouseExited(e: MouseEvent) {
                hoverPixel = null
                hoverRow = -1
                hoverColumn = -1
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.stroke = BasicStroke(lineThickness)

        drawTransparentBackground(g2D)

        for ((layerIndex, layer) in frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
            drawPixels(layerIndex, layer, g2D)
        }

        if (hoverPixel != null) {
            g2D.color = Color(Components.colourShades.selectedShade.red, Components.colourShades.selectedShade.green, Components.colourShades.selectedShade.blue, hoverOpacity)
            g2D.fillRect(hoverPixel!!.x, hoverPixel!!.y, hoverPixel!!.width, hoverPixel!!.height)
        }

        drawGrid(g2D)

        Components.toolbox.tool.render(g2D)
    }

    fun refreshMatrix(): MutableList<MutableList<Rectangle>> {
        val rMatrix = mutableListOf<MutableList<Rectangle>>()
        for (row in 0 until rowAmount) {
            val rectangleCells = mutableListOf<Rectangle>()
            for (column in 0 until columnAmount) {
                rectangleCells.add(Rectangle(row * pixelSize, column * pixelSize, pixelSize, pixelSize))
            }
            rMatrix.add(rectangleCells)
        }

        return rMatrix
    }

    fun drawPixels(layerIndex: Int, layer: Layer, g2D: Graphics2D) {
        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if ((Components.layerList.listModel.dataVector[layerIndex] as Vector<Any>)[2] as Boolean) {
                    if (layer.pixelMatrix[row][column].colour != null) {
                        g2D.color = layer.pixelMatrix[row][column].colour
                        val rectangle = rectangleMatrix[row][column]
                        g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, pixelSmooth, pixelSmooth)
                    }
                }
            }
        }
    }

    fun drawTransparentBackground(g2D: Graphics2D, rowCount: Int = rowAmount, columnCount: Int = columnAmount) {
        for (row in 0 until /* this.height */ rowCount * pixelSize / backgroundPixelSize) {
            for (column in 0 until /* this.width */ columnCount * pixelSize / backgroundPixelSize) {
                g2D.color = if (row % 2 == 0) {
                    if (column % 2 == 0) {
                        backgroundFillEven
                    }
                    else {
                        backgroundFillOdd
                    }
                }
                else {
                    if (column % 2 == 0) {
                        backgroundFillOdd
                    }
                    else {
                        backgroundFillEven
                    }
                }
                g2D.fillRect(column * backgroundPixelSize, row * backgroundPixelSize, backgroundPixelSize, backgroundPixelSize)
            }
        }
    }

    fun drawGrid(g2D: Graphics2D) {
        g2D.color = gridColour
        for (row in rectangleMatrix) {
            for (column in row) {
                g2D.drawRect(column.x, column.y, column.width, column.height)
            }
        }
    }
}
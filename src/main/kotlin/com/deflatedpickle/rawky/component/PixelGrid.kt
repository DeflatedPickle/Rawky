package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
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
        var pixelMatrix = Components.pixelGrid.initMatrix<Cell>()
        var visible = true

        enum class LockType {
            OFF,
            COLOUR,
            ALPHA,
            ALL
        }

        var lockType = LockType.OFF

        override fun toString(): String {
            return "Layer { $pixelMatrix, $visible, $lockType }"
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

    var scale = 1.0

    init {
        isOpaque = false

        rectangleMatrix = refreshMatrix()

        addMouseMotionListener(object : MouseMotionAdapter() {
            var lastPoint = Point()

            override fun mouseMoved(e: MouseEvent) {
                for ((rowIndex, row) in rectangleMatrix.withIndex()) {
                    for ((columnIndex, column) in row.withIndex()) {
                        if (column.contains(e.point.apply {
                                    // FIXME: Scale the translation
                                    translate(-(this@PixelGrid.width / 2 - this@PixelGrid.columnAmount * this@PixelGrid.pixelSize / 2),
                                            -(this@PixelGrid.height / 2 - this@PixelGrid.rowAmount * this@PixelGrid.pixelSize / 2))
                                })) {
                            hoverPixel = column
                            hoverRow = rowIndex
                            hoverColumn = columnIndex
                        }
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                mouseMoved(e)
                Components.toolbox.tool.mouseDragged(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.tool.performLeft(true, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool.performMiddle(true, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.tool.performRight(true, e.point, lastPoint, e.clickCount)
                }
                lastPoint = e.point
            }
        })

        addMouseListener(object : MouseAdapter() {
            var lastPoint = Point()

            override fun mousePressed(e: MouseEvent) {
                Components.toolbox.tool.mouseClicked(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.tool.performLeft(false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool.performMiddle(false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.tool.performRight(false, e.point, lastPoint, e.clickCount)
                }
                lastPoint = e.point
            }

            override fun mouseReleased(e: MouseEvent) {
                Components.toolbox.tool.mouseRelease(e.button)
            }

            override fun mouseEntered(e: MouseEvent) {
                cursor = Components.toolbox.tool.cursor
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
        g2D.translate(this.width / 2 - this.columnAmount * this.pixelSize / 2, this.height / 2 - this.rowAmount * this.pixelSize / 2)
        g2D.scale(this.scale, this.scale)

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

        if (!Components.actionHistory.list.isSelectionEmpty) {
            // TODO: Render a preview of the selected action on-top
            ActionStack.undoQueue[Components.actionHistory.list.selectedIndex].outline(g2D)
        }

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
                if (Components.layerList.isLayerHidden(layerIndex)) {
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

    inline fun <reified T>initMatrix(value: T? = null): MutableList<MutableList<T>> {
        val rowList = mutableListOf<MutableList<T>>()
        for (row in 0 until Components.pixelGrid.rowAmount) {
            val columnList = mutableListOf<T>()
            for (column in 0 until Components.pixelGrid.columnAmount) {
                columnList.add(value ?: T::class.constructors.first().call())
            }
            rowList.add(columnList)
        }
        return rowList
    }
}
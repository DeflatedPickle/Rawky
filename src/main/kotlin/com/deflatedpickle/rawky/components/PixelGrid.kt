package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.utils.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel

class PixelGrid : JPanel() {
    class Frame {
        var layerList = mutableListOf<Layer>()
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

        rectangleMatrix = zoom()

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
                performTool()
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                performTool()
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
    }

    fun zoom(): MutableList<MutableList<Rectangle>> {
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
                if (Components.layerList.listModel.dataVector[layerIndex][2] as Boolean) {
                    if (layer.pixelMatrix[row][column].colour != null) {
                        g2D.color = layer.pixelMatrix[row][column].colour
                        val rectangle = rectangleMatrix[row][column]
                        g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, pixelSmooth, pixelSmooth)
                    }
                }
            }
        }
    }

    fun drawTransparentBackground(g2D: Graphics2D) {
        for (row in 0 until /* this.height */ rowAmount * pixelSize / backgroundPixelSize) {
            for (column in 0 until /* this.width */ columnAmount * pixelSize / backgroundPixelSize) {
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

    fun performTool() {
        try {
            when (Components.toolbox.tool) {
                Toolbox.Tool.PENCIL -> {
                    if (!(Components.layerList.listModel.dataVector[Components.layerList.list.selectedRow][3] as Boolean)) {
                        frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[hoverRow][hoverColumn].colour = Components.colourShades.selectedShade
                    }
                }
                Toolbox.Tool.ERASER -> {
                    if (!(Components.layerList.listModel.dataVector[Components.layerList.list.selectedRow][3] as Boolean)) {
                        frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[hoverRow][hoverColumn].colour = null
                    }
                }
                Toolbox.Tool.PICKER -> {
                    Components.colourPicker.color = frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[hoverRow][hoverColumn].colour
                }
            }
        }
        catch (e: Exception) {
        }
    }
}
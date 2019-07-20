package com.deflatedpickle.rawky.components

import java.awt.*
import java.awt.event.*
import javax.swing.JPanel
import javax.swing.Timer

class PixelGrid : JPanel() {
    var pixelSize = 20
    var pixelSmooth = 0

    var hoverColour = Color.GRAY
    var hoverOpacity = 225 / 3

    var rowAmount = 8
    var columnAmount = 8

    var lineThickness = 1f

    var rectangleMatrix: MutableList<MutableList<Rectangle>>
    var pixelMatrix: MutableList<MutableList<Color?>>

    var hoverPixel: Rectangle? = null
    var hoverRow = 0
    var hoverColumn = 0

    init {
        isOpaque = false

        val rMatrix = mutableListOf<MutableList<Rectangle>>()
        val pMatrix = mutableListOf<MutableList<Color?>>()
        for (row in 0..rowAmount) {
            val rectangleCells = mutableListOf<Rectangle>()
            val pixelCells = mutableListOf<Color?>()
            for (column in 0..columnAmount) {
                rectangleCells.add(Rectangle(row * pixelSize, column * pixelSize, pixelSize, pixelSize))
                pixelCells.add(null)
            }
            rMatrix.add(rectangleCells)
            pMatrix.add(pixelCells)
        }
        rectangleMatrix = rMatrix
        pixelMatrix = pMatrix

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
                pixelMatrix[hoverRow][hoverColumn] = Components.colourShades.selectedShade
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                pixelMatrix[hoverRow][hoverColumn] = Components.colourShades.selectedShade
            }

            override fun mouseExited(e: MouseEvent) {
                hoverPixel = null
                hoverRow = -1
                hoverColumn = -1
            }
        })

        Timer(1000 / 60) {
            this.repaint()
        }.start()
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.color = Color.GRAY
        g2D.stroke = BasicStroke(lineThickness)

        // Draws the grid
        for (row in rectangleMatrix) {
            for (column in row) {
                g2D.drawRect(column.x, column.y, column.width, column.height)
            }
        }

        // Draws the pixels
        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if (pixelMatrix[row][column] != null) {
                    g2D.color = pixelMatrix[row][column]
                    val rectangle = rectangleMatrix[row][column]
                    g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, pixelSmooth, pixelSmooth)
                }
            }
        }

        if (hoverPixel != null) {
            g2D.color = Color(hoverColour.red, hoverColour.green, hoverColour.blue, hoverOpacity)
            g2D.fillRect(hoverPixel!!.x, hoverPixel!!.y, hoverPixel!!.width, hoverPixel!!.height)
        }
    }
}
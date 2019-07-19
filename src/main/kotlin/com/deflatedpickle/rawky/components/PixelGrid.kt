package com.deflatedpickle.rawky.components

import java.awt.*
import java.awt.event.*
import javax.swing.JPanel
import javax.swing.Timer

class PixelGrid : JPanel() {
    var pixelSize = 20
    var pixelSmooth = 0

    var rowAmount = 8
    var columnAmount = 8

    var lineThickness = 1f

    var rectangleMatrix: MutableList<MutableList<Rectangle>>
    var pixelMatrix: MutableList<MutableList<Color?>>

    var focusedPixel: Rectangle? = null
    var focusedRow = 0
    var focusedColumn = 0

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
                            focusedPixel = column
                            focusedRow = rowIndex
                            focusedColumn = columnIndex
                        }
                    }
                }
            }
        })

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                pixelMatrix[focusedRow][focusedColumn] = Color.BLACK
            }

            override fun mouseExited(e: MouseEvent) {
                focusedPixel = null
                focusedRow = -1
                focusedColumn = -1
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

        for (row in rectangleMatrix) {
            for (column in row) {
                g2D.drawRect(column.x, column.y, column.width, column.height)
            }
        }

        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if (pixelMatrix[row][column] != null) {
                    g2D.color = pixelMatrix[row][column]
                    val rectangle = rectangleMatrix[row][column]
                    g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, pixelSmooth, pixelSmooth)
                }
            }
        }

        if (focusedPixel != null) {
            g2D.color = Color.LIGHT_GRAY
            g2D.fillRect(focusedPixel!!.x, focusedPixel!!.y, focusedPixel!!.width, focusedPixel!!.height)
        }
    }
}
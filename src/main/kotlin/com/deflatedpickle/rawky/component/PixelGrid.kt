package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.*
import com.deflatedpickle.rawky.api.Enum
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import java.lang.Math.abs
import javax.swing.*
import kotlin.math.*

class PixelGrid : JPanel() {
    companion object {
        val INSTANCE = PixelGrid().apply {
            preferredSize = Dimension(2048, 2048)
        }

        val SCROLLABLE_INSTANCE = JScrollPane(INSTANCE)
    }

    val blankCursor = this.toolkit.createCustomCursor(
            BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            Point(),
            null)

    @Options
    object Settings {
        @IntRange(1, 100)
        @Tooltip("The size of the pixels")
        @JvmField
        var pixelSize = 20

        @IntRange(0, 42)
        @Tooltip("The smoothness of the pixels")
        @JvmField
        var pixelSmooth = 0

        @IntRange(1, 255)
        @Tooltip("The opacity of the hover mark")
        @JvmField
        var hoverOpacity = 255

        @DoubleRange(1.0, 6.0)
        @Tooltip("The thickness of the grid lines")
        @JvmField
        var lineThickness = 1.0

        @Colour
        @Tooltip("The colour of the grid lines")
        @JvmField
        var gridColour = Color.GRAY

        @IntRange(1, 100)
        @Tooltip("The size of the background pixels")
        @JvmField
        var backgroundPixelSize = pixelSize / 3

        @Enum("com.deflatedpickle.rawky.component.PixelGrid\$FillType")
        @Tooltip("The type of fill the background uses")
        @JvmField
        var backgroundFillType = FillType.GRID

        @Colour
        @Tooltip("The colour of the background even tiles")
        @JvmField
        var backgroundFillEven = Color.LIGHT_GRAY

        @Colour
        @Tooltip("The colour of the background odd tiles")
        @JvmField
        var backgroundFillOdd = Color.WHITE
    }

    interface MatrixItem<T> {
        var parent: T
    }

    class Frame {
        var layerList = mutableListOf<Layer>()

        override fun toString(): String {
            return "Frame { $layerList }"
        }
    }

    class Layer(override var parent: Frame) : MatrixItem<Frame> {
        var pixelMatrix = Components.pixelGrid.initMatrix<Layer, Cell>(parent = this)
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

    class Cell(override var parent: Layer) : MatrixItem<Layer> {
        var colour: Color? = null

        override fun toString(): String {
            return "Cell { $colour }"
        }
    }

    enum class FillType {
        ALL,
        GRID
    }

    var rowAmount = 16
    var columnAmount = 16

    var rectangleMatrix: MutableList<MutableList<Rectangle>>
    var frameList = mutableListOf<Frame>()

    var hoverPixel: Rectangle? = null
    var hoverRow = 0
    var hoverColumn = 0

    var scale = 1.0

    val lastCell = Point()
    val contextMenu = object : JPopupMenu() {
        init {
            add(object : JMenu("Quick Action") {
                init {
                    for (tool in Tool.list) {
                        add(JMenuItem(tool.name, tool.iconList[0]).apply {
                            addActionListener {
                                tool.performLeft(false, Point().also {
                                    it.x = lastCell.x / (Settings.pixelSize / 2)
                                    it.y = lastCell.y / (Settings.pixelSize / 2)
                                }, null, 1)
                            }
                        })
                    }
                }
            })
        }
    }

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
                                    // translate(-(this@PixelGrid.width / 2 - this@PixelGrid.columnAmount * this@PixelGrid.pixelSize / 2),
                                    //         -(this@PixelGrid.height / 2 - this@PixelGrid.rowAmount * this@PixelGrid.pixelSize / 2))
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
                Components.toolbox.indexList[e.button]?.mouseDragged(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.indexList[0]!!.perform(0, true, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.indexList[1]!!.perform(1, true, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.indexList[2]!!.perform(2, true, e.point, lastPoint, e.clickCount)
                }
                lastPoint = e.point
            }
        })

        addMouseListener(object : MouseAdapter() {
            var lastPoint = Point()

            override fun mousePressed(e: MouseEvent) {
                Components.toolbox.indexList[e.button]?.mouseClicked(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.indexList[0]!!.perform(0, false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.indexList[1]!!.perform(1, false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.indexList[2]!!.perform(2, false, e.point, lastPoint, e.clickCount)
                }
                lastPoint = e.point
            }

            override fun mouseReleased(e: MouseEvent) {
                Components.toolbox.indexList[0]!!.mouseRelease(e.button)
                Components.toolbox.indexList[1]!!.mouseRelease(e.button)
                Components.toolbox.indexList[2]!!.mouseRelease(e.button)
            }

            override fun mouseEntered(e: MouseEvent) {
                cursor = blankCursor
            }

            override fun mouseExited(e: MouseEvent) {
                hoverPixel = null
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D

        val bufferedImage = BufferedImage(min(this.visibleRect.x + this.visibleRect.width, (Settings.pixelSize * columnAmount) * ((this.scale * 10).toInt())), min(this.visibleRect.y + this.visibleRect.height, (Settings.pixelSize * rowAmount) * ((this.scale * 10).toInt())), BufferedImage.TYPE_INT_ARGB)
        val biG2D = bufferedImage.createGraphics()
        biG2D.scale(this.scale, this.scale)

        biG2D.stroke = BasicStroke(Settings.lineThickness.toFloat())

        drawTransparentBackground(biG2D)

        // Past Frames
        val pastGraphics = bufferedImage.createGraphics()
        pastGraphics.scale(this.scale, this.scale)
        pastGraphics.color = Components.animationTimeline.pastColour.color

        for (i in 1..abs(Components.animationTimeline.slider.pastSpinner.value as Int)) {
            if (Components.animationTimeline.list.selectedIndex - i >= 0) {
                pastGraphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f / ((i / abs(Components.animationTimeline.slider.pastSpinner.value as Int) + 1) + 1f))

                for ((layerIndex, layer) in frameList[abs(Components.animationTimeline.list.selectedIndex) - i].layerList.withIndex().reversed()) {
                    if (layerIndex >= 0) {
                        drawPixels(layerIndex, layer, pastGraphics, false)
                    }
                }
            }
        }
        pastGraphics.dispose()

        // Current Frames
        if (Components.animationTimeline.list.selectedIndex >= 0) {
            for ((layerIndex, layer) in frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
                if (layerIndex >= 0) {
                    drawPixels(layerIndex, layer, biG2D)
                }
            }
        }

        // Future Frames
        val postGraphics = bufferedImage.createGraphics()
        postGraphics.scale(this.scale, this.scale)
        postGraphics.color = Components.animationTimeline.postColour.color

        for (i in 1..Components.animationTimeline.slider.postSpinner.value as Int) {
            if (Components.animationTimeline.list.selectedIndex + i <= frameList.lastIndex) {
                postGraphics.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f / ((i / Components.animationTimeline.slider.postSpinner.value as Int + 1) + 1f))

                for ((layerIndex, layer) in frameList[Components.animationTimeline.list.selectedIndex + i].layerList.withIndex().reversed()) {
                    if (layerIndex >= 0) {
                        drawPixels(layerIndex, layer, postGraphics, false)
                    }
                }
            }
        }
        postGraphics.dispose()

        drawGrid(biG2D)

        if (!Components.actionHistory.list.isSelectionEmpty) {
            ActionStack.undoQueue[Components.actionHistory.list.selectedIndex].outline(biG2D)
        }

        val length = Components.toolbox.indexList.lastIndex
        for ((index, tool) in Components.toolbox.indexList.reversed().withIndex()) {
            if (mousePosition != null) {
                val size = if (index == length) 16 * 3 else 16 * 2

                if (index == length) {
                    biG2D.drawImage(tool?.cursor,
                            mousePosition.x + 6,
                            mousePosition.y,
                            size, size, this)
                }
                else {
                    val theta = (PI * 2) * index / Components.toolbox.indexList.count() - 1

                    val x = 14 * cos(theta).roundToInt()
                    val y = 14 * sin(theta).roundToInt()

                    biG2D.drawImage(tool?.cursor,
                            mousePosition.x + 44 + x,
                            mousePosition.y + y,
                            size, size, this)
                }
            }
        }

        for (tool in Components.toolbox.indexList.reversed()) {
            tool?.render(biG2D)
        }

        biG2D.dispose()
        g2D.drawRenderedImage(bufferedImage, null)
    }

    fun refreshMatrix(): MutableList<MutableList<Rectangle>> {
        val rMatrix = mutableListOf<MutableList<Rectangle>>()
        for (row in 0 until rowAmount) {
            val rectangleCells = mutableListOf<Rectangle>()
            for (column in 0 until columnAmount) {
                rectangleCells.add(Rectangle(column * Settings.pixelSize, row * Settings.pixelSize, Settings.pixelSize, Settings.pixelSize))
            }
            rMatrix.add(rectangleCells)
        }

        return rMatrix
    }

    fun drawPixels(layerIndex: Int, layer: Layer, g2D: Graphics2D, setColour: Boolean = true) {
        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if (!Components.layerList.isLayerHidden(layerIndex)) {
                    if (layer.pixelMatrix[row][column].colour != null) {
                        if (setColour) g2D.color = layer.pixelMatrix[row][column].colour
                        val rectangle = rectangleMatrix[row][column]

                        g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, Settings.pixelSmooth, Settings.pixelSmooth)
                    }
                }
            }
        }
    }

    fun drawTransparentBackground(g2D: Graphics2D, rowCount: Int = rowAmount, columnCount: Int = columnAmount, fillType: FillType = Settings.backgroundFillType, backgroundPixelDivider: Int = Settings.backgroundPixelSize) {
        val fill = when (fillType) {
            FillType.ALL -> {
                Pair(g2D.clipBounds.width, g2D.clipBounds.height)
            }
            FillType.GRID -> {
                // g2D.translate(this.width / 2 - this.columnAmount * this.pixelSize / 2, this.height / 2 - this.rowAmount * this.pixelSize / 2)
                Pair(rowCount, columnCount)
            }
        }

        for (row in 0 until fill.first * Settings.pixelSize / backgroundPixelDivider) {
            for (column in 0 until fill.second * Settings.pixelSize / backgroundPixelDivider) {
                g2D.color = if (row % 2 == column % 2) Settings.backgroundFillEven else Settings.backgroundFillOdd
                g2D.fillRect(column * backgroundPixelDivider, row * backgroundPixelDivider, backgroundPixelDivider, backgroundPixelDivider)
            }
        }

        when (Settings.backgroundFillType) {
            FillType.ALL -> {
                // g2D.translate(this.width / 2 - this.columnAmount * this.pixelSize / 2, this.height / 2 - this.rowAmount * this.pixelSize / 2)
            }
            else -> return
        }
    }

    fun drawGrid(g2D: Graphics2D) {
        g2D.color = Settings.gridColour
        for (row in rectangleMatrix) {
            for (column in row) {
                g2D.drawRect(column.x, column.y, column.width, column.height)
            }
        }
    }

    inline fun <reified P, reified T : MatrixItem<P>> initMatrix(value: T? = null, parent: Any): MutableList<MutableList<T>> {
        val rowList = mutableListOf<MutableList<T>>()
        for (row in 0 until Components.pixelGrid.rowAmount) {
            val columnList = mutableListOf<T>()
            for (column in 0 until Components.pixelGrid.columnAmount) {
                columnList.add(value ?: T::class.constructors.first().call(parent))
            }
            rowList.add(columnList)
        }
        return rowList
    }
}
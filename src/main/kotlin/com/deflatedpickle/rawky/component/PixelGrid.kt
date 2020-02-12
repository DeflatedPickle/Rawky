/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.Colour
import com.deflatedpickle.rawky.api.annotations.DoubleOpt
import com.deflatedpickle.rawky.api.annotations.Enum
import com.deflatedpickle.rawky.api.annotations.IntOpt
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.RedrawActive
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.api.component.ActionComponent
import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.LayoutMethod
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Polygon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import javax.swing.JButton
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSlider
import javax.swing.SwingUtilities
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import org.jdesktop.swingx.util.ShapeUtils

@RedrawActive
object PixelGrid : ActionComponent() {
    val slider = JSlider(25, 300).apply {
        this.value = 50
        addChangeListener {
            scale = this.value / 50.0
            PixelGrid.repaintWithChildren()
        }
    }

    val buttonZoomOut = JButton(Icons.zoomOut).apply {
        toolTipText = "Zoom Out"
        addActionListener {
            slider.value--
        }
    }

    val buttonZoomIn = JButton(Icons.zoomIn).apply {
        toolTipText = "Zoom In"
        addActionListener {
            slider.value++
        }
    }

    val SCROLLABLE_INSTANCE = JScrollPane(this.apply {
        preferredSize = Dimension(2048, 2048)
    }).apply {
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
    }

    object Shape {
        var points = 4
    }

    val blankCursor = this.toolkit.createCustomCursor(
            BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            Point(),
            null)

    @Options
    object Settings {
        var pixelSize = 20

        @IntOpt(1, 255)
        @Tooltip("The opacity of the hover mark")
        @JvmField
        var hoverOpacity = 255

        @DoubleOpt(1.0, 6.0)
        @Tooltip("The thickness of the grid lines")
        @JvmField
        var lineThickness = 1.0

        @Colour
        @Tooltip("The colour of the grid lines")
        @JvmField
        var gridColour = Color.GRAY

        @IntOpt(1, 20 * 8, 20)
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
        var backgroundFillEven: Color = Color.LIGHT_GRAY

        @Colour
        @Tooltip("The colour of the background odd tiles")
        @JvmField
        var backgroundFillOdd: Color = Color.WHITE
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
        var pixelMatrix = initMatrix<Layer?, Cell>(parent = this)
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

    class Cell(override var parent: Layer?) : MatrixItem<Layer?> {
        var colour: Color = defaultColour()

        var polygon: Polygon? = null

        // override fun toString(): String {
        //     return "Cell { $colour }"
        // }
    }

    enum class FillType {
        ALL,
        GRID
    }

    fun defaultColour(): Color = Color(0, 0, 0, 0)

    var rowAmount = 16
    var columnAmount = 16

    var rectangleMatrix: MutableList<MutableList<Polygon>>
    var frameList = mutableListOf<Frame>()

    var tempRectangleMatrix: MutableList<MutableList<Cell>>

    var hoverPixel: Polygon? = null
    var hoverRow = 0
    var hoverColumn = 0

    var scale = 1.0

    var layout = LayoutMethod.GRID

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
        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(buttonZoomOut, null),
                Pair(slider, fillX),
                Pair(buttonZoomIn, null)
        )

        rectangleMatrix = refreshMatrix()
        tempRectangleMatrix = initMatrix(parent = null)
        for ((rowIndex, row) in refreshMatrix().withIndex()) {
            for ((columnIndex, column) in row.withIndex()) {
                tempRectangleMatrix[rowIndex][columnIndex].polygon = column
            }
        }

        addMouseMotionListener(object : MouseMotionAdapter() {
            var lastPoint = Point()

            override fun mouseMoved(e: MouseEvent) {
                for ((rowIndex, row) in tempRectangleMatrix.withIndex()) {
                    for ((columnIndex, _) in row.withIndex()) {
                        with(tempRectangleMatrix[rowIndex][columnIndex]) {
                            val bounds = this.polygon!!.bounds
                            bounds.grow(3, 3)

                            if (!bounds.contains(e.point)) {
                                tempRectangleMatrix[rowIndex][columnIndex].colour = defaultColour()
                            }
                        }
                    }
                }

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

                            Components.toolbox.indexList[0]!!.mouseMoved(column, rowIndex, columnIndex)
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
                // cursor = blankCursor
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

        if (!ActionHistory.list.isSelectionEmpty) {
            ActionStack.undoQueue[ActionHistory.list.selectedIndex].outline(biG2D)
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
                } else {
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

        val toolGraphics = bufferedImage.createGraphics()
        toolGraphics.scale(this.scale, this.scale)
        for (tool in Components.toolbox.indexList.reversed()) {
            tool?.render(biG2D)
        }
        toolGraphics.dispose()

        biG2D.dispose()
        g2D.drawRenderedImage(bufferedImage, null)
    }

    fun refreshMatrix(): MutableList<MutableList<Polygon>> {
        val rMatrix = mutableListOf<MutableList<Polygon>>()
        for (row in 0 until rowAmount) {
            val y = if (row % 2 == 0) layout.columnOffsetEven else layout.columnOffsetOdd

            val rectangleCells = mutableListOf<Polygon>()
            for (column in 0 until columnAmount) {
                val x = if (column % 2 == 0) layout.rowOffsetEven else layout.rowOffsetOdd

                rectangleCells.add(
                        (ShapeUtils.generatePolygon(Shape.points,
                                Settings.pixelSize / 2, 0) as Polygon).apply {
                            translate(
                                    Settings.pixelSize / 2 + row * Settings.pixelSize + x,
                                    Settings.pixelSize / 2 + column * Settings.pixelSize + y
                            )
                        }
                )
            }
            rMatrix.add(rectangleCells)
        }

        return rMatrix
    }

    fun drawPixels(layerIndex: Int, layer: Layer, g2D: Graphics2D, setColour: Boolean = true, showHidden: Boolean = false) {
        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if (showHidden || !Components.layerList.isLayerHidden(layerIndex)) {
                    if (layer.pixelMatrix[row][column].colour != defaultColour()) {
                        if (setColour) g2D.color = layer.pixelMatrix[row][column].colour
                        val rectangle = rectangleMatrix[row][column]

                        if (Shape.points == 4) {
                            with(rectangle.bounds) {
                                this.grow(3, 3)
                                g2D.fillRect(this.x, this.y, this.width, this.height)
                            }
                        } else {
                            g2D.fillPolygon(rectangle)
                        }
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
                g2D.fillRect(
                        column * backgroundPixelDivider,
                        row * backgroundPixelDivider,
                        backgroundPixelDivider, backgroundPixelDivider
                )
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
                if (Shape.points == 4) {
                    with(column.bounds) {
                        this.grow(3, 3)
                        g2D.drawRect(this.x, this.y, this.width, this.height)
                    }
                } else {
                    g2D.drawPolygon(column)
                }
            }
        }
    }

    inline fun <reified P, reified T : MatrixItem<P>> initMatrix(value: T? = null, parent: Any? = null): MutableList<MutableList<T>> {
        val rowList = mutableListOf<MutableList<T>>()
        for (row in 0 until rowAmount) {
            val columnList = mutableListOf<T>()
            for (column in 0 until columnAmount) {
                columnList.add(value ?: T::class.constructors.first().call(parent))
            }
            rowList.add(columnList)
        }
        return rowList
    }
}

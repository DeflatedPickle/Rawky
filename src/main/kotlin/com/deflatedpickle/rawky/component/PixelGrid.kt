package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.EComponent
import com.deflatedpickle.rawky.guide.Guide
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.*

class PixelGrid : JPanel() {
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

    val pixelSize = 20
    var pixelSmooth = 0

    var backgroundPixelSize = pixelSize / 3
    var backgroundFillEven = Color.LIGHT_GRAY!!
    var backgroundFillOdd = Color.WHITE!!

    enum class FillType {
        ALL,
        GRID
    }

    var backgroundFillType = FillType.GRID

    var hoverOpacity = 255 // / 3

    var rowAmount = 16
    var columnAmount = 16

    var lineThickness = 1f
    var gridColour = Color.GRAY!!

    var rectangleMatrix: MutableList<MutableList<Rectangle>>
    var frameList = mutableListOf<Frame>()

    var hoverPixel: Rectangle? = null
    var hoverRow = 0
    var hoverColumn = 0

    var scale = 1.0

    val guideList = mutableListOf<Guide>()

    val lastCell = Point()
    val contextMenu = object : JPopupMenu() {
        init {
            add(object : JMenu("Quick Action") {
                init {
                    for (tool in Tool.list) {
                        add(JMenuItem(tool.name, tool.icon).apply {
                            addActionListener {
                                tool.performLeft(false, Point().also {
                                    it.x = lastCell.x / (pixelSize / 2)
                                    it.y = lastCell.y / (pixelSize / 2)
                                }, null, 1)
                            }
                        })
                    }
                }
            })

            add(object : JMenu("Guide") {
                init {
                    for (guide in Guide.list) {
                        add(JMenuItem(guide.getField("name").get("null").toString(), guide.getField("icon").get(null) as Icon?).apply {
                            addActionListener {
                                guideList.add(guide.getDeclaredConstructor(
                                        Guide.Orientation::class.java,
                                        Point::class.java,
                                        Dimension::class.java)
                                        .newInstance(
                                                Guide.Orientation.VERTICAL,
                                                Point(0, 0),
                                                Dimension(this@PixelGrid.width,
                                                        this@PixelGrid.height)))
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
                                    translate(-(this@PixelGrid.width / 2 - this@PixelGrid.columnAmount * this@PixelGrid.pixelSize / 2),
                                            -(this@PixelGrid.height / 2 - this@PixelGrid.rowAmount * this@PixelGrid.pixelSize / 2))
                                })) {
                            hoverPixel = column
                            hoverRow = rowIndex
                            hoverColumn = columnIndex
                        }
                    }
                }

                for (guide in guideList) {
                    for (rectangle in guide.rectangleList) {
                        if ((rectangle.clone() as Rectangle).apply {
                                    x -= guide.stroke.lineWidth.toInt()
                                    width += guide.stroke.lineWidth.toInt()
                                }.contains(e.point.apply {
                                    translate(-(width / 2 - (columnAmount * pixelSize) / 2), 0)
                                    translate(-(columnAmount * pixelSize) / 2, 0)
                                    translate(-guide.position.x, 0)
                                    this.y = 0
                                })) {
                            cursor = Cursor(Cursor.W_RESIZE_CURSOR)
                        }
                        else {
                            cursor = Components.toolbox.tool!!.cursor
                        }
                    }
                }
            }

            override fun mouseDragged(e: MouseEvent) {
                mouseMoved(e)
                Components.toolbox.tool!!.mouseDragged(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> {
                        var preOccupied = false
                        // TODO: The guide only seems to be able to move to the left 1 pixel, fix this
                        for (guide in guideList) {
                            for (rectangle in guide.rectangleList) {
                                if ((rectangle.clone() as Rectangle).apply {
                                            x -= guide.stroke.lineWidth.toInt() * 4
                                            width += guide.stroke.lineWidth.toInt() * 4
                                        }.contains(e.point.apply {
                                            translate(-(width / 2 - (columnAmount * pixelSize) / 2), 0)
                                            translate(-(columnAmount * pixelSize) / 2, 0)
                                            this.y = 0
                                        })) {
                                    preOccupied = true
                                    guide.position.setLocation(e.point.apply {
                                        translate(-(width / 2 - (columnAmount * pixelSize) / 2), 0)
                                        translate(-(columnAmount * pixelSize) / 2, 0)
                                        this.y = 0
                                    }.x, 0)
                                    break
                                }
                                else {
                                    preOccupied = false
                                }
                            }
                        }

                        if (!preOccupied) {
                            Components.toolbox.tool!!.performLeft(true, e.point, lastPoint, e.clickCount)
                        }
                    }
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool!!.performMiddle(true, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> Components.toolbox.tool!!.performRight(true, e.point, lastPoint, e.clickCount)
                }
                lastPoint = e.point
            }
        })

        addMouseListener(object : MouseAdapter() {
            var lastPoint = Point()

            override fun mousePressed(e: MouseEvent) {
                Components.toolbox.tool!!.mouseClicked(e.button)
                when {
                    SwingUtilities.isLeftMouseButton(e) -> Components.toolbox.tool!!.performLeft(false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isMiddleMouseButton(e) -> Components.toolbox.tool!!.performMiddle(false, e.point, lastPoint, e.clickCount)
                    SwingUtilities.isRightMouseButton(e) -> {
                        for (guide in guideList) {
                            for (rectangle in guide.rectangleList) {
                                if (rectangle.contains(e.point.apply {
                                            translate(-(width / 2 - (columnAmount * pixelSize) / 2), 0)
                                            translate(-(columnAmount * pixelSize) / 2, 0)
                                            this.y = 0
                                        })) {
                                    // TODO: Add a pop-up menu
                                }
                            }
                        }

                        Components.toolbox.tool!!.performRight(false, e.point, lastPoint, e.clickCount)
                    }
                }
                lastPoint = e.point
            }

            override fun mouseReleased(e: MouseEvent) {
                Components.toolbox.tool!!.mouseRelease(e.button)
            }

            override fun mouseEntered(e: MouseEvent) {
                // TODO: Draw the cursor on the canvas instead of setting the cursor
                cursor = Components.toolbox.tool!!.cursor
            }

            override fun mouseExited(e: MouseEvent) {
                hoverPixel = null
            }
        })
    }

    override fun getComponentPopupMenu(): JPopupMenu? {
        // TODO: Disable the items that require the hover pixel instead
        return if (hoverPixel != null) {
            lastCell.setLocation(this.hoverPixel!!.x, this.hoverPixel!!.y)
            this.contextMenu
        }
        else {
            null
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2D = g as Graphics2D
        g2D.scale(this.scale, this.scale)

        g2D.stroke = BasicStroke(lineThickness)

        // TODO: Put these in a list and add a drag-and-drop list of items to re-order the list
        drawTransparentBackground(g2D)

        if (Components.animationTimeline.list.selectedIndex >= 0) {
            for ((layerIndex, layer) in frameList[Components.animationTimeline.list.selectedIndex].layerList.withIndex().reversed()) {
                if (layerIndex >= 0) {
                    drawPixels(layerIndex, layer, g2D, EComponent.PIXEL_GRID)
                }
            }
        }

        drawGrid(g2D)

        if (!Components.actionHistory.list.isSelectionEmpty) {
            ActionStack.undoQueue[Components.actionHistory.list.selectedIndex].outline(g2D)
        }

        Components.toolbox.tool!!.render(g2D)

        val cachedStroke = g2D.stroke
        val cachedColour = g2D.color

        for (guide in this.guideList) {
            when (guide.orientation) {
                Guide.Orientation.HORIZONTAL -> g2D.translate(
                        rowAmount * pixelSize / 2,
                        -(this.height / 2 - this.rowAmount * this.pixelSize / 2)
                )

                Guide.Orientation.VERTICAL -> g2D.translate(
                        columnAmount * pixelSize / 2,
                        -(this.height / 2 - this.columnAmount * this.pixelSize / 2)
                )
            }

            // g2D.rotate(guide.angle, this@PixelGrid.width / 2.0, this@PixelGrid.height / 2.0)

            g2D.translate(guide.position.x, guide.position.y)

            // Yikes, looping the same list 3 times can't be efficient
            for (rectangle in guide.rectangleList) {
                g2D.stroke = guide.stroke
                g2D.color = guide.colour
            }

            guide.render(g2D)

            for (rectangle in guide.rectangleList) {
                g2D.stroke = cachedStroke
                g2D.color = cachedColour
            }

            g2D.translate(-guide.position.x, -guide.position.y)

            // g2D.rotate(-guide.angle, this@PixelGrid.width / 2.0, this@PixelGrid.height / 2.0)

            when (guide.orientation) {
                Guide.Orientation.HORIZONTAL -> g2D.translate(
                        -(rowAmount * pixelSize / 2),
                        this.height / 2 - this.rowAmount * this.pixelSize / 2
                )

                Guide.Orientation.VERTICAL -> g2D.translate(
                        -(columnAmount * pixelSize / 2),
                        this.height / 2 - this.columnAmount * this.pixelSize / 2
                )
            }
        }
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

    fun drawPixels(layerIndex: Int, layer: Layer, g2D: Graphics2D, component: EComponent) {
        for (row in 0 until rectangleMatrix.size) {
            for (column in 0 until rectangleMatrix[row].size) {
                if (!Components.layerList.isLayerHidden(layerIndex)) {
                    if (layer.pixelMatrix[row][column].colour != null) {
                        g2D.color = layer.pixelMatrix[row][column].colour
                        val rectangle = rectangleMatrix[row][column]

                        g2D.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, pixelSmooth, pixelSmooth)
                    }
                }
            }
        }
    }

    fun drawTransparentBackground(g2D: Graphics2D, rowCount: Int = rowAmount, columnCount: Int = columnAmount, fillType: FillType = this.backgroundFillType, backgroundPixelDivider: Int = this.backgroundPixelSize) {
        val fill = when (fillType) {
            FillType.ALL -> {
                Pair(g2D.clipBounds.height, g2D.clipBounds.width)
            }
            FillType.GRID -> {
                g2D.translate(this.width / 2 - this.columnAmount * this.pixelSize / 2, this.height / 2 - this.rowAmount * this.pixelSize / 2)
                Pair(rowCount, columnCount)
            }
        }

        for (row in 0 until fill.first * pixelSize / backgroundPixelDivider) {
            for (column in 0 until fill.second * pixelSize / backgroundPixelDivider) {
                g2D.color = if (row % 2 == column % 2) this.backgroundFillEven else this.backgroundFillOdd
                g2D.fillRect(column * backgroundPixelDivider, row * backgroundPixelDivider, backgroundPixelDivider, backgroundPixelDivider)
            }
        }

        when (this.backgroundFillType) {
            FillType.ALL -> {
                g2D.translate(this.width / 2 - this.columnAmount * this.pixelSize / 2, this.height / 2 - this.rowAmount * this.pixelSize / 2)
            }
            else -> return
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
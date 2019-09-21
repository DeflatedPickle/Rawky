package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.*
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.*

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    // TODO: Maybe merge this into Action, seems useful
    abstract class LockCheck(name: String) : ActionStack.Action(name) {
        val frame = Components.animationTimeline.list.selectedIndex
        val layer = Components.layerList.list.selectedRow

        val row = Components.pixelGrid.hoverRow
        val column = Components.pixelGrid.hoverColumn

        override fun check(): Boolean {
            return when (Components.layerList.layerLockType(layer)) {
                PixelGrid.Layer.LockType.OFF -> true
                PixelGrid.Layer.LockType.COLOUR -> Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour != null
                PixelGrid.Layer.LockType.ALPHA -> Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour == null
                PixelGrid.Layer.LockType.ALL -> false
            }
        }
    }

    // TODO: Move to an interface and a bunch of classes, when scripting is added
    enum class Tool(val icon: Icon, val cursor: Cursor, val selected: Boolean = false) {
        PENCIL(Icons.pencil, Toolkit.getDefaultToolkit().createCustomCursor(Icons.pencil.image, Point(8, 16), "Pencil"), true) {
            override fun performLeft(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {
                if (Components.pixelGrid
                                .frameList[Components.animationTimeline.list.selectedIndex]
                                .layerList[Components.layerList.list.selectedRow]
                                .pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn]
                                .colour
                        != Components.colourShades.selectedShade) {
                    val pixel = object : LockCheck(this.cursor.name) {
                        var oldValue = Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour

                        override fun perform() {
                            if (check()) {
                                Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = Components.colourShades.selectedShade
                            }
                        }

                        override fun cleanup() {
                            if (check()) {
                                Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = oldValue
                            }
                        }
                    }

                    if (pixel.check()) {
                        ActionStack.push(pixel)
                    }
                }
            }
        },
        ERASER(Icons.eraser, Toolkit.getDefaultToolkit().createCustomCursor(Icons.eraser.image, Point(8, 8), "Eraser")) {
            override fun performLeft(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {
                if (Components.pixelGrid
                                .frameList[Components.animationTimeline.list.selectedIndex]
                                .layerList[Components.layerList.list.selectedRow]
                                .pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn]
                                .colour
                        != null) {
                    val pixel = object : LockCheck(this.cursor.name) {
                        var oldValue = Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour

                        override fun perform() {
                            if (check()) {
                                Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = null
                            }
                        }

                        override fun cleanup() {
                            if (check()) {
                                Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = oldValue
                            }
                        }
                    }

                    if (pixel.check()) {
                        ActionStack.push(pixel)
                    }
                }
            }
        },
        PICKER(Icons.colour_picker, Toolkit.getDefaultToolkit().createCustomCursor(Icons.colour_picker.image, Point(8, 16), "Colour Picker")) {
            override fun performLeft(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {
                // TODO: Should colour picking push/pull to/from the undo/redo stack?
                Components.colourPicker.color = Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour
            }

            override fun render(g2D: Graphics2D) {
                val mouse = MouseInfo.getPointerInfo().location.apply {
                    SwingUtilities.convertPointFromScreen(this, Components.pixelGrid)
                    translate(-25, 20)
                }

                if (Components.pixelGrid.hoverRow >= 0 && Components.pixelGrid.hoverColumn >= 0) {
                    val layerList = Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList

                    for ((index, layer) in layerList.withIndex()) {
                        val hoverColour = layerList[index].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour

                        if (hoverColour != null) {
                            g2D.color = Color.BLACK
                            g2D.stroke = BasicStroke(4f)
                            g2D.drawRect(mouse.x, mouse.y, 20, 20)
                            g2D.color = hoverColour
                            g2D.fillRect(mouse.x, mouse.y, 20, 20)

                            break
                        }
                    }
                }
            }
        };

        // TODO: Is there still a point for these, given the method's below?
        open fun performLeft(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {}
        open fun performMiddle(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {}
        open fun performRight(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {}

        open fun mouseClicked(button: Int) {}
        open fun mouseDragged(button: Int) {
            if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() !is ActionStack.MultiAction) {
                ActionStack.push(ActionStack.MultiAction("MultiAction (${this.name.toLowerCase().capitalize()})"))
            }
        }
        open fun mouseRelease(button: Int) {
            if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() is ActionStack.MultiAction) {
                (ActionStack.undoQueue.last() as ActionStack.MultiAction).active = false
            }
        }

        open fun render(g2D: Graphics2D) {}
    }

    var tool = Tool.PENCIL

    init {
        this.layout = WrapLayout()

        val buttonGroup = ButtonGroup()

        for (t in Tool.values()) {
            this.add(JToggleButton(t.icon).apply {
                preferredSize = dimension
                toolTipText = t.cursor.name
                addActionListener { this@Toolbox.tool = t }

                buttonGroup.add(this)

                if (t.selected) {
                    buttonGroup.setSelected(this.model, true)
                }
            })
        }
    }
}
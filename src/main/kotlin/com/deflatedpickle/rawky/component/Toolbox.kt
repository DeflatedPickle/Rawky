package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Dimension
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.AbstractButton
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    open class LockCheck(name: String) : ActionStack.Action(name) {
        val frame = Components.animationTimeline.list.selectedIndex
        val layer = Components.layerList.list.selectedRow

        val row = Components.pixelGrid.hoverRow
        val column = Components.pixelGrid.hoverColumn

        override fun perform() {
            if (Components.layerList.isLayerLocked(layer)) {
                return
            }
        }

        override fun cleanup() {
            if (Components.layerList.isLayerLocked(layer)) {
                return
            }
        }
    }

    enum class Tool {
        PENCIL {
            override fun performLeft() {
                ActionStack.action(object : LockCheck("Pencil") {
                    override fun perform() {
                        super.perform()
                        Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = Components.colourShades.selectedShade
                    }

                    override fun cleanup() {
                        super.cleanup()
                        Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = null
                    }
                })
            }
        },
        ERASER {
            override fun performLeft() {
                ActionStack.action(object : LockCheck("Eraser") {
                    val colour = Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour

                    override fun perform() {
                        super.perform()
                        Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = null
                    }

                    override fun cleanup() {
                        super.cleanup()
                        Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour = colour
                    }
                })
            }
        },
        PICKER {
            override fun performLeft() {
                // TODO: Should colour picking push/pull to/from the undo/redo stack?
                Components.colourPicker.color = Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour
            }
        };

        open fun performLeft() {}
        open fun performMiddle() {}
        open fun performRight() {}
    }

    var tool = Tool.PENCIL

    val pencilButton = JToggleButton(Icons.pencil).apply {
        preferredSize = dimension
        toolTipText = "Pencil"
        addActionListener { tool = Tool.PENCIL }
    }
    val eraserButton = JToggleButton(Icons.eraser).apply {
        preferredSize = dimension
        toolTipText = "Eraser"
        addActionListener { tool = Tool.ERASER }
    }
    val pickerButton = JToggleButton(Icons.colour_picker).apply {
        preferredSize = dimension
        toolTipText = "Colour Picker"
        addActionListener { tool = Tool.PICKER }
    }

    init {
        this.layout = WrapLayout()

        this.add(pencilButton)
        this.add(eraserButton)
        this.add(pickerButton)

        val buttonGroup = ButtonGroup()
        for (i in components) {
            buttonGroup.add(i as AbstractButton)
        }
    }
}
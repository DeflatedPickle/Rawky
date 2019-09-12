package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Dimension
import javax.swing.AbstractButton
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    enum class Tool {
        PENCIL {
            override fun performLeft() {
                if (!Components.layerList.isLayerLocked()) {
                    Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour = Components.colourShades.selectedShade
                }
            }
        },
        ERASER {
            override fun performLeft() {
                if (!Components.layerList.isLayerLocked()) {
                    Components.pixelGrid.frameList[Components.animationTimeline.list.selectedIndex].layerList[Components.layerList.list.selectedRow].pixelMatrix[Components.pixelGrid.hoverRow][Components.pixelGrid.hoverColumn].colour = null
                }
            }
        },
        PICKER {
            override fun performLeft() {
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
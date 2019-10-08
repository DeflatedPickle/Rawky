package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Dimension
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton
import javax.swing.SwingUtilities

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    // TODO: Maybe merge this into Action, seems useful
    abstract class LockCheck(name: String) : ActionStack.Action(name) {
        val frame = Components.animationTimeline.list.selectedIndex
        val layer = Components.layerList.list.selectedRow

        val row = Components.pixelGrid.hoverRow
        val column = Components.pixelGrid.hoverColumn

        override fun check(): Boolean {
            val colour = Components.pixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour
            return when (Components.layerList.layerLockType(layer)) {
                PixelGrid.Layer.LockType.OFF -> true
                PixelGrid.Layer.LockType.COLOUR -> colour != null
                PixelGrid.Layer.LockType.ALPHA -> colour == null
                PixelGrid.Layer.LockType.ALL -> false
            }
        }
    }

    var tool: Tool? = null
        set(value) {
            SwingUtilities.invokeLater {
                field = value
                Components.toolOptions.relayout()
            }
        }

    init {
        this.layout = WrapLayout()

        val buttonGroup = ButtonGroup()

        for (t in Tool.list) {
            this.add(JToggleButton(t.icon).apply {
                preferredSize = dimension
                toolTipText = t.cursor.name
                addActionListener { this@Toolbox.tool = t }

                buttonGroup.add(this)

                if (t.selected) {
                    tool = t
                    buttonGroup.setSelected(this.model, true)
                }
            })
        }
    }
}
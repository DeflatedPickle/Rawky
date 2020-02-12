/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton
import uk.co.timwise.wraplayout.WrapLayout

class Toolbox : Component() {
    // TODO: Maybe merge this into Action, seems useful
    abstract class LockCheck(name: String) : ActionStack.Action(name) {
        val frame = Components.animationTimeline.list.selectedIndex
        val layer = Components.layerList.table.selectedRow

        val row = PixelGrid.hoverRow
        val column = PixelGrid.hoverColumn

        override fun check(): Boolean {
            val colour = PixelGrid.frameList[frame].layerList[layer].pixelMatrix[row][column].colour
            return when (Components.layerList.layerLockType(layer)) {
                PixelGrid.Layer.LockType.OFF -> true
                PixelGrid.Layer.LockType.COLOUR -> colour != PixelGrid.defaultColour()
                PixelGrid.Layer.LockType.ALPHA -> colour == PixelGrid.defaultColour()
                PixelGrid.Layer.LockType.ALL -> false
            }
        }
    }

    // TODO: This could be expanded to support more buttons
    var indexList = arrayOfNulls<Tool>(Tool.list.size).toMutableList()

    enum class Group(val dimension: Dimension, val group: ButtonGroup) {
        PRIMARY(Dimension(28, 28), ButtonGroup()),
        MIDDLE(Dimension(10, 28), ButtonGroup()),
        SECONDARY(Dimension(18, 28), ButtonGroup())
    }

    init {
        this.layout = WrapLayout()

        for (t in Tool.list) {
            JPanel().also {
                it.layout = GridBagLayout()

                for ((gi, g) in Group.values().withIndex()) {
                    it.add(JToggleButton(t.iconList[gi]).apply {
                        preferredSize = g.dimension
                        toolTipText = "${g.name.toLowerCase().capitalize()} ${t.name}"
                        addActionListener {
                            indexList[gi] = t

                            Components.toolOptions.relayout()
                        }

                        g.group.add(this)

                        if (t.selected) {
                            indexList[gi] = t
                            g.group.setSelected(this.model, true)
                        }
                    }, GridBagConstraints().apply {
                        weightx = 1.0
                        weighty = 1.0
                        fill = GridBagConstraints.BOTH

                        if (g == Group.SECONDARY) {
                            gridwidth = GridBagConstraints.REMAINDER
                        }
                    })
                }
                add(it)
            }
        }
    }
}

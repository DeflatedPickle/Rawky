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
import javax.swing.SwingUtilities

class Toolbox : Component() {
    // TODO: Maybe merge this into Action, seems useful
    abstract class LockCheck(name: String, canMerge: Boolean = true) : ActionStack.Action(name, canMerge) {
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
    var toolIndexList = arrayOfNulls<Tool>(3).toMutableList()

    enum class Group(val dimension: Dimension, val buttonGroup: ButtonGroup) {
        PRIMARY(Dimension(28, 28), ButtonGroup()),
        MIDDLE(Dimension(10, 28), ButtonGroup()),
        SECONDARY(Dimension(18, 28), ButtonGroup())
    }

    init {
        this.layout = WrapLayout()

        // Loops through all the tools, sorted alphabetically
        // NOTE: Could add a program option for how tools are sorted, eg. most used
        for (tool in Tool.list.sortedBy { it.name }) {
            add(JPanel().also {
                it.layout = GridBagLayout()

                for ((groupIndex, group) in Group.values().withIndex()) {
                    it.add(JToggleButton(tool.iconList[groupIndex]).apply {
                        preferredSize = group.dimension
                        toolTipText = "${group.name.toLowerCase().capitalize()} ${tool.name}"

                        addActionListener {
                            toolIndexList[groupIndex] = tool
                        }

                        group.buttonGroup.add(this)

                        // Checks the tool belongs to a group and that the group's index is the same as the current one
                        if (tool.group != null && tool.group.ordinal == groupIndex) {
                            toolIndexList[groupIndex] = tool

                            SwingUtilities.invokeLater {
                                group.buttonGroup.setSelected(this.model, true)
                            }
                        }
                    }, GridBagConstraints().apply {
                        weightx = 1.0
                        weighty = 1.0
                        fill = GridBagConstraints.BOTH

                        if (group.ordinal % 3 == 2) {
                            gridwidth = GridBagConstraints.REMAINDER
                        }
                    })
                }
            })
        }
    }
}

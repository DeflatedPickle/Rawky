/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.Icons
import java.awt.BorderLayout
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JList

class ActionHistory : Component() {
    val buttonUndo = JButton(Icons.undo).apply {
        toolTipText = "Undo Action"
        addActionListener { ActionStack.undo() }
    }

    val buttonRedo = JButton(Icons.redo).apply {
        toolTipText = "Redo Action"
        addActionListener { ActionStack.redo() }
    }

    val buttonTrash = JButton(Icons.trash).apply {
        toolTipText = "Delete Action"
        addActionListener { ActionStack.pop(Components.actionHistory.list.selectedIndex) }
    }

    val listModel = DefaultListModel<String>()
    // TODO: Replace with a JTree
    // TODO: Add a tooltip that shows the change the action made
    val list = JList<String>(listModel).apply {
        // TODO: Could probably be replaced with a MultiAction
        val action = object : ActionStack.Action("Mass Undo") {
            val actionList = mutableListOf<ActionStack.Action>()

            override fun perform() {
                for (i in 0 until listModel.size - this@apply.selectedIndex) {
                    val action = ActionStack.undoQueue.last()
                    this.actionList.add(action)
                    ActionStack.undoQueue.remove(action)
                    Components.actionHistory.listModel.remove(Components.actionHistory.listModel.size - 1)
                    action.cleanup()
                }
            }

            override fun cleanup() {
                for (i in this.actionList.reversed()) {
                    i.perform()
                    ActionStack.undoQueue.add(i)
                    Components.actionHistory.listModel.addElement(i.name)
                }
                this.actionList.clear()
            }

            override fun outline(g2D: Graphics2D) {
                for (i in this.actionList.reversed()) {
                    i.outline(g2D)
                }
            }
        }

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                when (e.clickCount) {
                    1 -> PixelGrid.repaint()
                    3 -> ActionStack.push(action)
                }
            }
        })
    }

    init {
        toolbarWidgets[BorderLayout.PAGE_START] = listOf(
                Pair(buttonUndo, null),
                Pair(buttonRedo, null),
                Pair("---", null),
                Pair(buttonTrash, null)
        )

        isOpaque = false
        layout = BorderLayout()

        add(list)
    }
}

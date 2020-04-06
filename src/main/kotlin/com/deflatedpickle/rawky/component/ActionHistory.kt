/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.component.ActionComponent
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.EComponent
import com.deflatedpickle.rawky.util.Icons
import com.deflatedpickle.rawky.util.extension.toCamelCase
import com.deflatedpickle.rawky.util.extension.toConstantCase
import java.awt.BorderLayout
import java.awt.Graphics2D
import java.awt.event.ItemEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JList

object ActionHistory : Component() {
    var currentWidget: ActionComponent = PixelGrid

    val comboboxValues = EComponent.values()
            .filter {
                Components.componentToInstance(
                        EComponent.values()[it.ordinal]
                ) is ActionComponent
            }
            .map { e -> e.toString().toCamelCase() }
            .toTypedArray()
    val comboboxCurrentWidget = JComboBox<String>(comboboxValues).apply {
        addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                val component = Components.componentToInstance(
                        EComponent.valueOf(((it.source as JComboBox<*>).selectedItem as String).toConstantCase())
                )

                if (component is ActionComponent) {
                    refresh(component)
                } else {
                    listModel.removeAllElements()
                }
            }
        }
    }

    val buttonNext = JButton(Icons.forwardArrow).apply {
        toolTipText = "Next Undo Stack"
        addActionListener {
            if (comboboxCurrentWidget.selectedIndex + 1 < comboboxCurrentWidget.itemCount) {
                comboboxCurrentWidget.selectedIndex++
            } else {
                comboboxCurrentWidget.selectedIndex = 0
            }
        }
    }

    val buttonPrevious = JButton(Icons.backArrow).apply {
        toolTipText = "Previous Undo Stack"
        addActionListener {
            if (comboboxCurrentWidget.selectedIndex - 1 >= 0) {
                comboboxCurrentWidget.selectedIndex--
            } else {
                comboboxCurrentWidget.selectedIndex = comboboxCurrentWidget.itemCount - 1
            }
        }
    }

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
        addActionListener { ActionStack.pop(list.selectedIndex) }
    }

    fun refresh(widget: ActionComponent = currentWidget) {
        listModel.removeAllElements()

        for (i in widget.actionStack.undoQueue) {
            listModel.addElement(i.name)
        }

        list.selectedIndex = listModel.size() - 1
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
                    listModel.remove(listModel.size - 1)
                    action.cleanup()
                }
            }

            override fun cleanup() {
                for (i in this.actionList.reversed()) {
                    i.perform()
                    ActionStack.undoQueue.add(i)
                    listModel.addElement(i.name)
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
        
        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(buttonPrevious, null),
                Pair(comboboxCurrentWidget, fillX),
                Pair(buttonNext, null)
        )

        add(list)
    }
}

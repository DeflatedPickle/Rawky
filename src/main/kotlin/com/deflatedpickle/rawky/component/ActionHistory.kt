package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel

class ActionHistory : JPanel() {
    val listModel = DefaultListModel<String>()
    // TODO: Replace with a JTree
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
        }

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    ActionStack.push(action)
                }
            }
        })
    }

    init {
        isOpaque = false
        layout = BorderLayout()

        add(list)
    }
}
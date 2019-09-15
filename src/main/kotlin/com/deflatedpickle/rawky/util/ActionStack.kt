package com.deflatedpickle.rawky.util

import java.util.*

object ActionStack {
    abstract class Action(val name: String) {
        /**
         * A check to see if the action should happen
         */
        open fun check(): Boolean {
            return true
        }

        /**
         * Performed on redo
         */
        open fun perform() {
            if (!check()) {
                return
            }
        }

        /**
         * Performed on undo
         */
        abstract fun cleanup()
    }

    val undoQueue = mutableListOf<Action>()
    val redoQueue = mutableListOf<Action>()

    fun push(it: Action) {
        if (!redoQueue.isEmpty()) {
            redoQueue.clear()
        }

        it.perform()
        Components.actionHistory.listModel.addElement(it.name)

        undoQueue.add(it)

        Components.actionHistory.list.selectedIndex = Components.actionHistory.listModel.size() - 1
    }

    fun undo() {
        if (undoQueue.isNotEmpty()) {
            redoQueue.add(undoQueue.last().apply {
                undoQueue.remove(this)
                Components.actionHistory.listModel.remove(Components.actionHistory.listModel.size - 1)
                cleanup()
            })

            Components.actionHistory.list.selectedIndex = Components.actionHistory.listModel.size() - 1
        }
    }

    fun redo() {
        if (redoQueue.isNotEmpty()) {
            undoQueue.add(redoQueue.last().apply {
                redoQueue.remove(this)
                Components.actionHistory.listModel.addElement(name)
                perform()
            })

            Components.actionHistory.list.selectedIndex = Components.actionHistory.listModel.size() - 1
        }
    }

    fun delete(index: Int) {
        Components.actionHistory.listModel.remove(index)
        undoQueue.remove(undoQueue.elementAt(index))

        Components.actionHistory.list.selectedIndex = Components.actionHistory.listModel.size() - 1
    }
}
/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Graphics2D

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
        abstract fun perform()

        /**
         * Performed on undo
         */
        abstract fun cleanup()

        /**
         * Draws an outline around this action
         */
        abstract fun outline(g2D: Graphics2D)
    }

    class MultiAction(name: String) : Action(name) {
        val stack = mutableListOf<Action>()
        var active = true

        override fun perform() {
            for (i in this.stack) {
                i.perform()
            }
        }

        override fun cleanup() {
            for (i in this.stack) {
                i.cleanup()
            }
        }

        override fun outline(g2D: Graphics2D) {
            for (i in this.stack) {
                i.outline(g2D)
            }
        }
    }

    val undoQueue = mutableListOf<Action>()
    val redoQueue = mutableListOf<Action>()

    fun push(it: Action) {
        if (redoQueue.isNotEmpty()) {
            redoQueue.clear()
        }

        it.perform()

        if (undoQueue.isNotEmpty() && undoQueue.last() is MultiAction && (undoQueue.last() as MultiAction).active) {
            (undoQueue.last() as MultiAction).stack.add(it)
        } else {
            Components.actionHistory.listModel.addElement(it.name)
            undoQueue.add(it)
        }

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

            PixelGrid.repaintWithChildren()
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

            PixelGrid.repaintWithChildren()
        }
    }

    fun pop(index: Int): Action {
        Components.actionHistory.listModel.remove(index)
        val item = undoQueue.elementAt(index)
        undoQueue.remove(item)

        Components.actionHistory.list.selectedIndex = Components.actionHistory.listModel.size() - 1

        return item
    }
}

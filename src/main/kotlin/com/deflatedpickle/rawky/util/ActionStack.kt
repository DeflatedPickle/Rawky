/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.ActionHistory
import com.deflatedpickle.rawky.component.PixelGrid
import java.awt.Graphics2D

class ActionStack {
    companion object {
        fun push(action: Action) {
            ActionHistory.currentWidget.actionStack.push(action)

            ActionHistory.refresh()
        }

        fun undo() {
            if (ActionHistory.currentWidget.actionStack.undo()) {
                ActionHistory.refresh()
            }
        }

        fun redo() {
            if (ActionHistory.currentWidget.actionStack.redo()) {
                ActionHistory.refresh()
            }
        }

        fun pop(index: Int): Action {
            ActionHistory.listModel.remove(index)

            val pop = ActionHistory.currentWidget.actionStack.pop(index)

            ActionHistory.list.selectedIndex = ActionHistory.listModel.size() - 1
            ActionHistory.refresh()

            return pop
        }

        val undoQueue: MutableList<Action>
            get() = ActionHistory.currentWidget.actionStack.undoQueue
    }

    abstract class Action(val name: String, var canMerge: Boolean = true) {
        /**
         * A check to see if the action should happen
         */
        open fun check(): Boolean {
            return true
        }

        open fun mouseDown() {}

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
            undoQueue.add(it)
        }
    }

    fun undo(): Boolean {
        if (undoQueue.isNotEmpty()) {
            redoQueue.add(undoQueue.last().apply {
                undoQueue.remove(this)
                cleanup()
            })

            PixelGrid.repaintWithChildren()
        }

        return undoQueue.isNotEmpty()
    }

    fun redo(): Boolean {
        if (redoQueue.isNotEmpty()) {
            undoQueue.add(redoQueue.last().apply {
                redoQueue.remove(this)
                perform()
            })

            PixelGrid.repaintWithChildren()
        }

        return redoQueue.isNotEmpty()
    }

    fun pop(index: Int): Action {
        val item = undoQueue.elementAt(index)
        undoQueue.remove(item)

        return item
    }
}

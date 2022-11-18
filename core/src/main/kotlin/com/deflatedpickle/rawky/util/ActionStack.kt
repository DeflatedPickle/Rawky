/* Copyright (c) 2020 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.event.EventPopAction
import com.deflatedpickle.rawky.event.EventPushAction
import com.deflatedpickle.rawky.event.EventRedoAction
import com.deflatedpickle.rawky.event.EventUndoAction
import java.awt.Graphics2D
import javax.swing.tree.DefaultMutableTreeNode

object ActionStack {
    val undoQueue = mutableListOf<Action>()
    val redoQueue = mutableListOf<Action>()

    fun push(it: Action) {
        if (redoQueue.isNotEmpty()) {
            redoQueue.clear()
        }

        it.perform()
        EventPushAction.trigger(it)

        if (undoQueue.isNotEmpty() && undoQueue.last() is MultiAction && (undoQueue.last() as MultiAction).active) {
            (undoQueue.last() as MultiAction).stack.add(it)
        } else {
            undoQueue.add(it)
        }
    }

    fun undo(): Boolean {
        if (undoQueue.isNotEmpty()) {
            redoQueue.add(
                undoQueue.last().apply {
                    undoQueue.remove(this)
                    cleanup()
                    EventUndoAction.trigger(this)
                }
            )
        }

        return undoQueue.isNotEmpty()
    }

    fun redo(): Boolean {
        if (redoQueue.isNotEmpty()) {
            undoQueue.add(
                redoQueue.last().apply {
                    redoQueue.remove(this)
                    perform()
                    EventRedoAction.trigger(this)
                }
            )
        }

        return redoQueue.isNotEmpty()
    }

    fun pop(index: Int): Action {
        val item = undoQueue.elementAt(index)
        undoQueue.remove(item)
        EventPopAction.trigger(item)

        return item
    }

    abstract class Action(val name: String, var canMerge: Boolean = true) {
        override fun toString() = name.capitalize()

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

    open class MultiAction(name: String) : Action(name) {
        lateinit var root: DefaultMutableTreeNode

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
}

package com.deflatedpickle.rawky.util

import java.util.*

object ActionStack {
    abstract class Action(val name: String) : Comparable<Action> {
        abstract fun perform()
        abstract fun cleanup()

        override fun compareTo(other: Action): Int {
            return -1
        }
    }

    val undoQueue = PriorityQueue<Action>()
    val redoQueue = PriorityQueue<Action>()

    fun action(it: Action) {
        if (!redoQueue.isEmpty()) {
            redoQueue.clear()
        }

        it.perform()
        Components.actionHistory.listModel.addElement(it.name)

        undoQueue.add(it)
    }

    fun undo() {
        if (undoQueue.isNotEmpty()) {
            redoQueue.add(undoQueue.poll().apply {
                Components.actionHistory.listModel.remove(Components.actionHistory.listModel.size - 1)
                cleanup()
            })
        }
    }

    fun redo() {
        if (redoQueue.isNotEmpty()) {
            undoQueue.add(redoQueue.poll().apply {
                Components.actionHistory.listModel.addElement(name)
                perform()
            })
        }
    }
}
package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import org.reflections.Reflections
import java.awt.Cursor
import java.awt.Graphics2D
import java.awt.Point
import java.lang.reflect.Modifier
import javax.swing.Icon

abstract class Tool(val name: String, val icon: Icon, val cursor: Cursor, val selected: Boolean = false) {
    companion object {
        val list = mutableListOf<Tool>()

        init {
            val reflections = Reflections("com.deflatedpickle.rawky.tool")

            for (i in reflections.getSubTypesOf(Tool::class.java)) {
                if (!Modifier.isAbstract(i.modifiers)) {
                    with(i.newInstance()) {
                        list.add(this)
                    }
                }
            }
        }
    }

    open fun performLeft(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}

    open fun performMiddle(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {}
    open fun performRight(dragged: Boolean, point: Point, lastPoint: Point, clickCount: Int) {}

    open fun mouseClicked(button: Int) {}
    open fun mouseDragged(button: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() !is ActionStack.MultiAction) {
            ActionStack.push(ActionStack.MultiAction("MultiAction (${this.name.toLowerCase().capitalize()})"))
        }
    }

    open fun mouseRelease(button: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() is ActionStack.MultiAction) {
            (ActionStack.undoQueue.last() as ActionStack.MultiAction).active = false
        }
    }

    open fun render(g2D: Graphics2D) {}
}
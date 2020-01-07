/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import java.lang.reflect.Modifier
import javax.swing.Icon
import javax.swing.ImageIcon
import org.reflections.Reflections

abstract class Tool(val name: String, var iconList: List<Icon>, val cursor: Image, val selected: Boolean = false) {
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

    init {
        val tempList = iconList.toMutableList()
        while (tempList.size < Toolbox.Group.values().size) {
            tempList.add(ImageIcon())
        }
        iconList = tempList
    }

    open fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}

    open fun performLeft(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}

    open fun performMiddle(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}
    open fun performRight(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}

    open fun mouseClicked(button: Int) {}
    open fun mouseDragged(button: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() !is ActionStack.MultiAction) {
            ActionStack.push(ActionStack.MultiAction("MultiAction (${this.name.toLowerCase().capitalize()})").apply {
                stack.add(ActionStack.pop(ActionStack.undoQueue.size - 1))
            })
        }
    }

    open fun mouseRelease(button: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() is ActionStack.MultiAction) {
            (ActionStack.undoQueue.last() as ActionStack.MultiAction).active = false
        }
    }

    open fun render(g2D: Graphics2D) {}
}

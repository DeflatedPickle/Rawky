/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.tool

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.component.Toolbox
import com.deflatedpickle.rawky.util.ActionStack
import com.deflatedpickle.rawky.util.Components
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import java.awt.Polygon
import java.lang.reflect.Modifier
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import org.reflections.Reflections

abstract class Tool(
    val name: String,
    var iconList: List<Icon>,
    val cursor: Image,
    val group: Toolbox.Group? = null
) {
    companion object {
        val list = mutableListOf<Tool>()

        init {
            val reflections = Reflections("com.deflatedpickle.rawky.tool")

            for (i in reflections.getSubTypesOf(Tool::class.java)) {
                if (!Modifier.isAbstract(i.modifiers)) {
                    with(i.declaredConstructors.first().newInstance()) {
                        list.add(this as Tool)
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

        SwingUtilities.invokeLater {
            if (this.group != null) {
                Components.toolOptions.relayout(0)
            }
        }
    }

    open fun perform(button: Int, dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}
    open fun release(button: Int, point: Point, lastPoint: Point?) {}

    open fun performLeft(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}
    open fun releaseLeft(point: Point, lastPoint: Point?) {}

    open fun performMiddle(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}
    open fun performRight(dragged: Boolean, point: Point, lastPoint: Point?, clickCount: Int) {}

    open fun mouseClicked(button: Int) {}
    open fun mouseClicked(button: Int, polygon: Polygon, row: Int, column: Int, clickCount: Int) {}

    open fun mouseDragged(button: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() !is ActionStack.MultiAction && ActionStack.undoQueue.last().canMerge) {
            ActionStack.push(ActionStack.MultiAction("MultiAction (${this.name.toLowerCase().capitalize()})").apply {
                stack.add(ActionStack.pop(ActionStack.undoQueue.size - 1))
            })
        }
    }

    open fun mouseDragged(button: Int, polygon: Polygon?, row: Int, column: Int) {
        mouseDragged(button)
    }

    open fun mouseRelease(button: Int, polygon: Polygon?, row: Int, column: Int) {
        if (ActionStack.undoQueue.isNotEmpty() && ActionStack.undoQueue.last() is ActionStack.MultiAction) {
            (ActionStack.undoQueue.last() as ActionStack.MultiAction).active = false
        }
    }

    open fun mouseMoved(polygon: Polygon, row: Int, column: Int) {}

    open fun render(g2D: Graphics2D) {}

    var isCached = false
    open fun <T> process(
        x0: Int,
        y0: Int,
        x1: Int?,
        y1: Int?,
        cellMatrix: MutableList<MutableList<PixelGrid.Cell>>
    ):
            MutableMap<PixelGrid.Cell, T> = mutableMapOf()
}

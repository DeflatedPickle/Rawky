/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.component

import com.deflatedpickle.rawky.api.annotations.RedrawActive
import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import org.jdesktop.swingx.JXPanel

open class Component : JXPanel() {
    val sensitiveChildren = mutableListOf<Component>()

    var toolbarWidgets = mutableMapOf<String, List<*>>()

    lateinit var componentFrame: ComponentFrame

    init {
        for (i in this::class.annotations) {
            when (i) {
                is RedrawActive -> {
                    this.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent?) { this@Component.repaint() }
                        override fun mousePressed(e: MouseEvent?) { this@Component.repaint() }
                        override fun mouseMoved(e: MouseEvent?) { this@Component.repaint() }
                        override fun mouseDragged(e: MouseEvent?) { this@Component.repaint() }
                    }.apply { addMouseMotionListener(this) })
                }
                is RedrawSensitive<*> -> {
                    i.parent.objectInstance?.addMouseListener(
                            object : MouseAdapter() {
                                override fun mouseReleased(e: MouseEvent?) { this@Component.repaint() }
                                override fun mousePressed(e: MouseEvent?) { this@Component.repaint() }
                                override fun mouseDragged(e: MouseEvent?) { this@Component.repaint() }
                            }.apply { i.parent.objectInstance?.addMouseMotionListener(this) })

                    i.parent.objectInstance?.sensitiveChildren?.add(this)
                }
            }
        }
    }

    fun repaintWithChildren() {
        this.repaint()
        this.sensitiveChildren.forEach { it.repaint() }
    }

    open fun afterInit() {
        componentFrame.makeToolbars()
    }
}

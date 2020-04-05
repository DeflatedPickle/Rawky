/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.component

import com.deflatedpickle.rawky.api.annotations.RedrawActive
import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import org.jdesktop.swingx.JXPanel
import javax.swing.SwingUtilities

open class Component : JXPanel() {
    companion object {
        val fillX = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }
    }

    private val sensitiveChildren = mutableListOf<Component>()

    var toolbarWidgets = mutableMapOf<String, List<Pair<*, GridBagConstraints?>>>()

    lateinit var componentFrame: ComponentFrame

    init {
        this.isOpaque = false
        this.layout = BorderLayout()

        this.isFocusable = true
        this.isEnabled = true

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

    override fun repaint() {
        super.invalidate()
        super.revalidate()

        super.repaint()
    }

    fun repaintWithChildren() {
        this.repaint()
        this.sensitiveChildren.forEach { it.repaint() }
    }

    open fun afterInit() {
        componentFrame.makeToolbars()
    }
}

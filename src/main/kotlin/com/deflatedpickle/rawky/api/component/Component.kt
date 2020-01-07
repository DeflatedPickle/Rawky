package com.deflatedpickle.rawky.api.component

import com.deflatedpickle.rawky.api.annotations.RedrawActive
import com.deflatedpickle.rawky.api.annotations.RedrawSensitive
import org.jdesktop.swingx.JXPanel
import java.awt.event.*

open class Component : JXPanel() {
    val sensitiveChildren = mutableListOf<Component>()

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
}
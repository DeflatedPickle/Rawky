package com.deflatedpickle.rawky.ui.component

import com.deflatedpickle.rawky.api.plugin.Plugin
import com.deflatedpickle.rawky.api.redraw.RedrawActive
import org.jdesktop.swingx.JXPanel
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JScrollPane

/**
 * A superclass of [JPanel] providing utilities for Rawky
 */
abstract class RawkyPanel : JXPanel() {
    /**
     * The plugin this component belongs to
     */
    lateinit var plugin: Plugin

    /**
     * The [JScrollPane] this panel was added to
     */
    lateinit var scrollPane: JScrollPane

    /**
     * A component to hold this one. Helpful for adding toolbars
     */
    lateinit var componentHolder: RawkyPanelHolder

    init {
        this.isOpaque = true

        this.processAnnotations()
    }

    private fun processAnnotations() {
        for (annotation in this::class.annotations) {
            when (annotation) {
                is RedrawActive -> {
                    this.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent) = repaint()
                        override fun mousePressed(e: MouseEvent) = repaint()
                        override fun mouseMoved(e: MouseEvent) = repaint()
                        override fun mouseDragged(e: MouseEvent) = repaint()
                    }.apply { addMouseMotionListener(this) })
                }
            }
        }
    }

    override fun repaint() {
        super.invalidate()
        super.revalidate()

        super.repaint()
    }
}
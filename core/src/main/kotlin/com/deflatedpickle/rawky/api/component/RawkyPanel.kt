package com.deflatedpickle.rawky.api.component

import com.deflatedpickle.rawky.api.plugin.Plugin
import org.jdesktop.swingx.JXPanel
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Rectangle
import javax.swing.JScrollPane
import javax.swing.Scrollable

/**
 * A superclass of [JPanel] providing utilities for Rawky
 */
open class RawkyPanel : JXPanel() {
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
    }
}
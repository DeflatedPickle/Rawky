/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.util.Components
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.SwingConstants
import org.jdesktop.swingx.JXPanel

class ToolOptions : Component() {
    object StickEast : GridBagConstraints() {
        init {
            anchor = EAST
        }
    }

    object FillHorizontalFinishLine : GridBagConstraints() {
        init {
            fill = HORIZONTAL
            weightx = 1.0
            gridwidth = REMAINDER
            insets = Insets(2, 2, 2, 2)
        }
    }

    object FillHorizontal : GridBagConstraints() {
        init {
            fill = BOTH
            weightx = 1.0
        }
    }

    object FinishLine : GridBagConstraints() {
        init {
            gridwidth = REMAINDER
        }
    }

    val tabList = arrayOfNulls<JXPanel>(3).toMutableList()

    val tabbedPane = JTabbedPane().apply {
        this.tabLayoutPolicy = JTabbedPane.SCROLL_TAB_LAYOUT

        // Tool options are singletons, we need to relayout the panel on-change in case any settings have been changed
        this.addChangeListener { relayout(this.selectedIndex) }

        for ((groupIndex, group) in Toolbox.Group.values().withIndex()) {
            this.addTab(group.name.toLowerCase().capitalize(), JScrollPane(JXPanel().apply {
                this.layout = GridBagLayout()

                this.scrollableTracksViewportWidth = true
                this.scrollableTracksViewportHeight = false

                tabList[groupIndex] = this
            }).apply { border = BorderFactory.createEmptyBorder() })
        }
    }

    init {
        this.add(tabbedPane)
    }

    fun relayout(tabIndex: Int) {
        with(this.tabList[tabIndex]!!) {
            this.removeAll()

            this.add(JLabel(Components.toolbox.toolIndexList[tabIndex]!!::class.java.simpleName.capitalize() + ":").apply {
                font = font.deriveFont(14f)
                horizontalAlignment = SwingConstants.CENTER
            }, FillHorizontalFinishLine)

            for (clazz in Components.toolbox.toolIndexList[tabIndex]!!::class.java.declaredClasses) {
                if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                    for (field in clazz.fields) {
                        if (field.name != "INSTANCE") {
                            Components.processAnnotations(this, field)
                        }
                    }

                    for (subClazz in clazz.classes) {
                        Components.processAnnotations(this, subClazz)
                    }
                }
            }

            this.invalidate()
            this.revalidate()
            this.repaint()
        }
    }
}

/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.tool.Tool
import com.deflatedpickle.rawky.util.Components
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.SwingConstants
import kotlin.reflect.full.allSupertypes

class ToolOptions : Component() {
    object StickEast : GridBagConstraints() {
        init {
            anchor = EAST
        }
    }

    object FillHorizontal : GridBagConstraints() {
        init {
            fill = BOTH
            weightx = 1.0
            gridwidth = REMAINDER
        }
    }

    init {
        this.layout = GridBagLayout()

        scrollableTracksViewportWidth = true
        scrollableTracksViewportHeight = false
    }

    fun relayout() {
        this.removeAll()

        // TODO: Add a tabbed pane and add a tab for each tool
        this.add(JLabel(Components.toolbox.indexList[0]!!::class.java.simpleName.capitalize() + ":").apply {
            font = font.deriveFont(14f)
            horizontalAlignment = SwingConstants.CENTER
        }, FillHorizontal)

        for (clazz in Components.toolbox.indexList[0]!!::class.java.declaredClasses) {
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

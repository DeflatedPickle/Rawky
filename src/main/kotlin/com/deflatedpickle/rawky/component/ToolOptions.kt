package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.Range
import com.deflatedpickle.rawky.util.Components
import org.jdesktop.swingx.JXPanel
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.SwingConstants

class ToolOptions : JXPanel() {
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

        this.add(JLabel(Components.toolbox.tool!!::class.java.simpleName.capitalize() + ":").apply {
            font = font.deriveFont(14f)
            horizontalAlignment = SwingConstants.CENTER
        }, FillHorizontal)

        for (clazz in Components.toolbox.tool!!::class.java.declaredClasses) {
            if (clazz.annotations.map { it.annotationClass == Options::class }.contains(true)) {
                for (field in clazz.fields) {
                    if (field.name != "INSTANCE") {
                        this.add(JLabel(field.name.capitalize() + ":"))
                        for (annotation in field.annotations) {
                            val widget: Component = when (annotation) {
                                // TODO: Add more argument types
                                is Range -> {
                                    JSlider(annotation.min, annotation.max).apply {
                                        value = field.getInt(null)

                                        addChangeListener {
                                            field.set(null, value)
                                        }

                                        this@ToolOptions.add(this, FillHorizontal)
                                    }
                                }
                                else -> JLabel("$annotation is unsupported!").apply { foreground = Color.RED }
                            }
                        }
                    }
                }
            }
        }

        this.invalidate()
        this.revalidate()
        this.repaint()
    }
}
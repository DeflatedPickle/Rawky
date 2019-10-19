package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.DoubleRange
import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.IntRange
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.widget.DoubleSlider
import com.deflatedpickle.rawky.widget.Slider
import org.jdesktop.swingx.JXPanel
import java.awt.Color
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
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
                        Components.processAnnotations(this, field)
                    }
                }
            }
        }

        this.invalidate()
        this.revalidate()
        this.repaint()
    }
}
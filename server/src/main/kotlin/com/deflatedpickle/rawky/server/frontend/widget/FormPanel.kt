/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTitledSeparator
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel

fun form(init: FormPanel.() -> Unit): FormPanel = FormPanel().apply { init() }

class FormPanel : JXPanel() {
    init {
        this.isOpaque = false
        this.layout = GridBagLayout()
        this.preferredSize = Dimension(200, 20)
    }

    fun category(label: String) {
        add(JXTitledSeparator(label), FillHorizontalFinishLine)
    }

    fun <T : JComponent> widget(label: String, component: T): Pair<JLabel, T> {
        val labelComp = JXLabel("$label:")
        add(labelComp, StickEast)
        add(component, FillHorizontalFinishLine)
        return Pair(labelComp, component)
    }
}

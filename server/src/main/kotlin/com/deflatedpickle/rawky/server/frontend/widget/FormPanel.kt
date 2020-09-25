package com.deflatedpickle.rawky.server.frontend.widget

import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.deflatedpickle.rawky.ui.constraints.StickEastFinishLine
import com.deflatedpickle.tosuto.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField

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

    fun field(label: String, component: JTextField): Pair<JLabel, JTextField> {
        val labelComp = JXLabel("$label:")
        add(labelComp, StickEast)
        add(component, FillHorizontalFinishLine)
        return Pair(labelComp, component)
    }

    fun check(component: JCheckBox): JCheckBox {
        add(component, StickEastFinishLine)
        return component
    }
}
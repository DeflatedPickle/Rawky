package com.deflatedpickle.rawky.colourhistory

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import org.jdesktop.swingx.WrapLayout
import java.awt.BorderLayout
import java.awt.GridBagLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

object ColourHistoryPanel : PluginPanel() {
    val panel = JPanel().apply {
        layout = WrapLayout()
    }

    init {
        layout = GridBagLayout()

        add(JScrollPane(panel), FillBothFinishLine)
    }
}
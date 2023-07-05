/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourhistory

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import uk.co.timwise.wraplayout.WrapLayout
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

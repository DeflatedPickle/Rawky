/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.undulation.constraints.StickWestFinishLine
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.time.Year
import javax.swing.JLabel
import javax.swing.JPanel

class AboutDialog : TaskDialog(Haruhi.window, "About Rawky") {
    init {
        setCommands(StandardCommand.OK.derive("Close"))

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JLabel("Rawky").apply { font = font.deriveFont(24f) }, StickWestFinishLine)
                add(JLabel("Copyright © 2019 - ${Year.now().value} DeflatedPickle under the MIT license").apply { font = font.deriveFont(11f) }, StickWestFinishLine)
            }
    }
}

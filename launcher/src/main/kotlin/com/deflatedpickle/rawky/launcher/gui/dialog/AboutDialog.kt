/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.undulation.constraints.FillVerticalStickEast
import com.deflatedpickle.undulation.constraints.StickWest
import com.deflatedpickle.undulation.constraints.StickWestFinishLine
import com.deflatedpickle.undulation.widget.HyperLabel
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.net.URI
import java.time.Year
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class AboutDialog : TaskDialog(Haruhi.window, "About Rawky") {
    init {
        setCommands(StandardCommand.OK.derive("Close"))

        this.fixedComponent =
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JLabel("Rawky").apply { font = font.deriveFont(24f) }, StickWestFinishLine)
                add(JLabel("Copyright © 2019 - ${Year.now().value} DeflatedPickle under the MIT license").apply { font = font.deriveFont(11f) }, StickWestFinishLine)

                add(HyperLabel("Repository", URI("https://github.com/DeflatedPickle/Rawky")), StickWest)
                add(JSeparator(SwingConstants.VERTICAL), FillVerticalStickEast)
                add(HyperLabel("License", URI("https://github.com/DeflatedPickle/Rawky/blob/rewrite/LICENSE")), StickWest)
                add(JSeparator(SwingConstants.VERTICAL), FillVerticalStickEast)
                add(HyperLabel("Wiki", URI("https://github.com/DeflatedPickle/Rawky/wiki")), StickWest)
            }
    }
}

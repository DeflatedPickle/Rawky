/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.launcher.gui.dialog

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.rawky.launcher.LauncherPlugin
import com.deflatedpickle.rawky.launcher.gui.Window
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FillVerticalStickEast
import com.deflatedpickle.undulation.constraints.StickWest
import com.deflatedpickle.undulation.constraints.StickWestFinishLine
import com.deflatedpickle.undulation.widget.HyperLabel
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.Image
import java.net.URI
import java.time.Year
import javax.imageio.ImageIO
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class AboutDialog : TaskDialog(Haruhi.window, "About Rawky") {
    init {
        setCommands(StandardCommand.OK.derive("Close"))

        this.fixedComponent =
            JPanel().apply {
                layout = GridBagLayout()

                Window.image?.let {
                    add(JLabel(ImageIcon(ImageIO.read(LauncherPlugin::class.java.getResource(it)).getScaledInstance(128, 128, Image.SCALE_SMOOTH))))
                }
                add(JLabel("Rawky").apply { font = font.deriveFont(24f) }, StickWestFinishLine)
                add(JLabel("Copyright © 2019 - ${Year.now().value} DeflatedPickle under the MIT license").apply { font = font.deriveFont(11f) }, StickWestFinishLine)

                add(JPanel().apply {
                    add(HyperLabel("Repository", URI("https://github.com/DeflatedPickle/Rawky")), StickWest)
                    add(Box.createGlue())
                    add(HyperLabel("License", URI("https://github.com/DeflatedPickle/Rawky/blob/rewrite/LICENSE")), StickWest)
                    add(Box.createGlue())
                    add(HyperLabel("Wiki", URI("https://github.com/DeflatedPickle/Rawky/wiki")), StickWest)
                }, StickWestFinishLine)
            }
    }
}

/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.alee.laf.WebLookAndFeel
import com.bulenkov.darcula.DarculaLaf
import com.deflatedpickle.rawky.api.annotations.Enum
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Setter
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.dialogue.Settings as SettingsDialogue
import com.deflatedpickle.rawky.util.Components
import javax.swing.JFrame
import javax.swing.LookAndFeel
import javax.swing.SwingUtilities
import javax.swing.UIManager
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel

class Window : JFrame("Rawky") {
    @Options
    object Settings {
        @Setter("themeSetter")
        @Enum("com.deflatedpickle.rawky.component.Window\$LAF")
        @Tooltip("Changes how the program looks")
        @JvmField
        var laf = LAF.NATIVE

        fun themeSetter() {
            if (laf.string != null) {
                UIManager.setLookAndFeel(laf.string)
            } else if (laf.laf != null) {
                UIManager.setLookAndFeel(laf.laf)
            }

            SwingUtilities.updateComponentTreeUI(Components.frame)
            SwingUtilities.updateComponentTreeUI(PixelGrid.contextMenu)
            SwingUtilities.updateComponentTreeUI(SettingsDialogue)
        }
    }

    enum class LAF(val string: String? = null, val laf: LookAndFeel? = null) {
        METAL(UIManager.getCrossPlatformLookAndFeelClassName()),
        NATIVE(UIManager.getSystemLookAndFeelClassName()),
        DARCULA(laf = DarculaLaf()),
        GRAPHITE_ELECTRIC(laf = SubstanceGraphiteElectricLookAndFeel()),
        WEB(laf = WebLookAndFeel())
    }

    init {
        SwingUtilities.invokeLater { Settings.themeSetter() }
    }
}

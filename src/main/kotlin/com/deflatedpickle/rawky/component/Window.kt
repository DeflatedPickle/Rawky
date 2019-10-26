package com.deflatedpickle.rawky.component

import com.alee.laf.WebLookAndFeel
import com.bulenkov.darcula.DarculaLaf
import com.deflatedpickle.rawky.api.Enum
import com.deflatedpickle.rawky.api.Options
import com.deflatedpickle.rawky.api.Setter
import com.deflatedpickle.rawky.api.Tooltip
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.dialogue.Settings as SettingsDialogue
import org.pushingpixels.substance.api.skin.SubstanceGraphiteElectricLookAndFeel
import javax.swing.JFrame
import javax.swing.LookAndFeel
import javax.swing.SwingUtilities
import javax.swing.UIManager

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
            }
            else if (laf.laf != null) {
                UIManager.setLookAndFeel(laf.laf)
            }

            SwingUtilities.updateComponentTreeUI(Components.frame)
            SwingUtilities.updateComponentTreeUI(Components.pixelGrid.contextMenu)
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
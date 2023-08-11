/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.colourpalette

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.functions.extensions.div
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.Color
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.filechooser.FileNameExtensionFilter

@Plugin(
    value = "colour_palette",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a picker for colour palettes
    """,
    type = PluginType.COMPONENT,
    component = ColourPalettePanel::class,
)
@Suppress("unused")
object ColourPalettePlugin {
    val folder = (File(".") / "palette" / "colour").apply { mkdirs() }
    val registry = Registry<String, PaletteParser<Color>>()

    private val chooser =
        JFileChooser(File(".")).apply {
            EventProgramFinishSetup.addListener {
                for ((k, v) in registry) {
                    addChoosableFileFilter(FileNameExtensionFilter("${v.name} (*.$k)", k))
                }
            }
        }

    init {
        EventChangeTheme.addListener { ColourPalettePanel.updateUIRecursively() }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.FILE.name) as JMenu).apply {
                    val index = menuComponents.indexOf(menuComponents.filterIsInstance<JMenuItem>().first { it.text == "Import..." })

                    add("Import Colour Palette", MonoIcon.FOLDER_NEW, index = index) { importColourPalette() }
                }
            }
        }
    }

    private fun importColourPalette() {
        if (chooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            val i = chooser.selectedFile

            if (i.isFile) {
                registry[i.extension]?.let { pp -> ColourPalettePanel.combo.addItem(pp.parse(i)) }
            }
        }
    }
}

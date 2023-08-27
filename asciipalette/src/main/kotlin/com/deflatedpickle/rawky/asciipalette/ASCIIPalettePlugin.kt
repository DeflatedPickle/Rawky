/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.asciipalette

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.functions.extensions.div
import com.deflatedpickle.marvin.registry.Registry
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.palette.PaletteParser
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.rawky.grid.ascii.ASCIICellPlugin
import com.deflatedpickle.rawky.grid.ascii.collection.ASCIICell
import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.extensions.JMenuItem
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.font.GlyphVector
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.filechooser.FileNameExtensionFilter

@Plugin(
    value = "ascii_palette",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a picker for ASCII characters
    """,
    type = PluginType.COMPONENT,
    component = ASCIIPalettePanel::class,
)
@Suppress("unused")
object ASCIIPalettePlugin {
    val folder = (File(".") / "palette" / "ascii").apply { mkdirs() }
    val registry = Registry<String, PaletteParser<GlyphVector>>()

    private val chooser =
        JFileChooser(File(".")).apply {
            isAcceptAllFileFilterUsed = false

            EventProgramFinishSetup.addListener {
                for ((k, v) in registry) {
                    addChoosableFileFilter(FileNameExtensionFilter("${v.name} (*.$k)", k))
                }
            }
        }

    private val fillAsciiItem = JMenuItem(
        "Fill with ASCII Character",
        message = "Change every cell to the currently selected ASCII character",
        enabled = false,
    ) {
        fill()
    }

    init {
        EventChangeTheme.addListener { ASCIIPalettePanel.updateUIRecursively() }

        EventCreateDocument.addListener {
            fillAsciiItem.isEnabled = CellProvider.current == ASCIICellPlugin
        }

        EventOpenDocument.addListener {
            fillAsciiItem.isEnabled = CellProvider.current == ASCIICellPlugin
        }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.FILE.name) as JMenu).apply {
                    val index = menuComponents.indexOf(menuComponents.filterIsInstance<JMenuItem>().first { it.text == "Import..." })

                    add("Import ASCII Palette...", index = index) { importASCIIPalette() }
                }

                (get(MenuCategory.EDIT.name) as JMenu).apply {
                    add(fillAsciiItem)
                }
            }
        }
    }

    private fun importASCIIPalette() {
        if (chooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            val i = chooser.selectedFile

            if (i.isFile) {
                registry[i.extension]?.let { pp -> ASCIIPalettePanel.combo.addItem(pp.parse(i)) }
            }
        }
    }

    private fun fill() {
        RawkyPlugin.document?.let { doc ->
            for (frame in doc) {
                for (layer in frame) {
                    for (cell in layer.child) {
                        (cell as ASCIICell).apply {
                            content = ASCIICellPlugin.current
                            colour = PixelCellPlugin.current
                        }
                    }
                }
            }

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            EventUpdateGrid.trigger(layer.child)
        }
    }
}

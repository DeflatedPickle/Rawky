/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.rawky.tilepalette

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
import com.deflatedpickle.rawky.grid.tile.TileCellPlugin
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.functions.JMenuItem
import com.deflatedpickle.undulation.functions.extensions.add
import com.deflatedpickle.undulation.functions.extensions.updateUIRecursively
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.filechooser.FileNameExtensionFilter

@Plugin(
    value = "tile_palette",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a picker for colour palettes
    """,
    type = PluginType.COMPONENT,
    component = TilePalettePanel::class,
)
object TilePalettePlugin {
    val folder = (File(".") / "palette" / "tile").apply { mkdirs() }
    val registry = Registry<String, PaletteParser<Image>>()

    private val chooser =
        JFileChooser(File(".")).apply {
            isAcceptAllFileFilterUsed = false

            EventProgramFinishSetup.addListener {
                for ((k, v) in registry) {
                    addChoosableFileFilter(FileNameExtensionFilter("${v.name} (*.$k)", k))
                }
            }
        }

    private val fillTileItem = JMenuItem(
        "Fill with Tile",
        message = "Change every cell to the currently selected tile",
        enabled = false,
    ) {
        fill(TileCellPlugin.current)
    }

    init {
        EventChangeTheme.addListener { TilePalettePanel.updateUIRecursively() }

        EventCreateDocument.addListener {
            fillTileItem.isEnabled = CellProvider.current == TileCellPlugin
        }

        EventOpenDocument.addListener {
            fillTileItem.isEnabled = CellProvider.current == TileCellPlugin
        }

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.FILE.name) as JMenu).apply {
                    val index = menuComponents.indexOf(menuComponents.filterIsInstance<JMenuItem>().first { it.text == "Import..." })

                    add("Import Tile Palette...", index = index) { importTilePalette() }
                }

                (get(MenuCategory.EDIT.name) as JMenu).apply {
                    add(fillTileItem)
                }
            }
        }
    }

    private fun importTilePalette() {
        if (chooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
            val i = chooser.selectedFile

            if (i.isFile) {
                registry[i.extension]?.let { pp -> TilePalettePanel.combo.addItem(pp.parse(i)) }
            }
        }
    }

    private fun fill(tile: BufferedImage) {
        RawkyPlugin.document?.let { doc ->
            for (frame in doc) {
                for (layer in frame) {
                    for (cell in layer.child) {
                        cell.content = tile
                    }
                }
            }

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            EventUpdateGrid.trigger(layer.child)
        }
    }
}

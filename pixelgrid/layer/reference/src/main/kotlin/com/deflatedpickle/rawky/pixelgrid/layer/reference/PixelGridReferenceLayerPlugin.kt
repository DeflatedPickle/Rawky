/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.rawky.pixelgrid.layer.reference

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.pixelgrid.PixelGridPanel
import com.deflatedpickle.rawky.pixelgrid.api.LayerCategory
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer
import com.deflatedpickle.rawky.pixelgrid.api.PaintLayer.Companion.registry
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.DrawUtil
import com.deflatedpickle.undulation.functions.extensions.add
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JPopupMenu
import javax.swing.filechooser.FileNameExtensionFilter

@ExperimentalSerializationApi
@Plugin(
    value = "pixel_grid_reference_layer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Paints a set image
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@core#*",
        "deflatedpickle@pixelgrid#*",
    ],
    settings = ReferenceSettings::class,
)
@Suppress("unused")
object PixelGridReferenceLayerPlugin : PaintLayer {
    override val name = "Reference"
    override val layer = LayerCategory.UNDER_DECO

    private var image: BufferedImage? = null
    private var composite: AlphaComposite? = null

    private val imageChooser = JFileChooser(File(".")).apply {
        for ((d, e) in mapOf(
            "Portable Network Graphics" to listOf("png"),
            "Graphics Interchange Format" to listOf("gif"),
            "Joint Photographic Experts Group" to
                    listOf("jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
        )) {
            addChoosableFileFilter(
                FileNameExtensionFilter(
                    "$d (${e.joinToString { "*.$it" }})", *e.toTypedArray(),
                ),
            )
        }
    }

    init {
        registry["reference"] = this

        EventProgramFinishSetup.addListener {
            RegistryUtil.get(MenuCategory.MENU.name)?.apply {
                (get(MenuCategory.TOOLS.name) as JMenu).apply {
                    add(
                        "Add/Change Reference Image...",
                        index = 0,
                    ) {
                        if (imageChooser.showOpenDialog(Haruhi.window) == JFileChooser.APPROVE_OPTION) {
                            ConfigUtil.getSettings<ReferenceSettings>("deflatedpickle@pixel_grid_reference_layer#*")
                                ?.let { settings ->
                                    settings.file = imageChooser.selectedFile
                                    image = null
                                }

                            PixelGridPanel.repaint()
                        }
                    }

                    add(
                        "Clear Reference Image",
                        index = 1
                    ) {
                        ConfigUtil.getSettings<ReferenceSettings>("deflatedpickle@pixel_grid_reference_layer#*")
                            ?.let { settings ->
                                settings.file = null
                                image = null
                            }

                        PixelGridPanel.repaint()
                    }

                    popupMenu.insert(JPopupMenu.Separator(), 2)
                }
            }
        }
    }

    override fun paint(doc: RawkyDocument?, frame: Int, layer: Int, g2d: Graphics2D) {
        doc?.let {
            if (doc.selectedIndex >= doc.children.size) return

            ConfigUtil.getSettings<ReferenceSettings>("deflatedpickle@pixel_grid_reference_layer#*")
                ?.let { settings ->
                    if (!settings.enabled) return

                    if (settings.file != null) {
                        if (composite == null) {
                            composite = AlphaComposite.SrcOver.derive(settings.opacity)
                        }

                        if (image == null) {
                            image = ImageIO.read(settings.file)
                        }

                        val factor = DrawUtil.getScaleFactor(
                            doc.columns.toDouble(),
                            doc.rows.toDouble(),
                            image!!.width.toDouble() / Grid.pixel,
                            image!!.height.toDouble() / Grid.pixel,
                        )

                        g2d.composite = composite
                        g2d.scale(factor, factor)
                        g2d.drawImage(image, 0, 0, null)
                    }
                }
        }
    }

    private fun drawBackground(
        g: Graphics2D,
        grid: Grid,
        size: Int,
        evenColour: Color,
        oddColour: Color,
    ) {
        for (r in 0 until grid.rows * Grid.pixel / size) {
            for (c in 0 until grid.columns * Grid.pixel / size) {
                g.color = if (r % 2 == c % 2) evenColour else oddColour
                g.fillRect(c * size, r * size, size, size)
            }
        }
    }
}

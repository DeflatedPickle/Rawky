@file:Suppress("unused", "UNUSED_PARAMETER")

package com.deflatedpickle.rawky.pixelgrid.export.image.imageio

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.ActionUtil
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Plugin(
    value = "imageio",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Export images supported by ImageIO
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@pixel_grid#*",
    ],
)
object ImageIOPlugin : Exporter, Importer, Opener {
    override val name = "ImageIO"
    override val extensions = mapOf(
        "Portable Network Graphics" to listOf("png"),
        "Graphics Interchange Format" to listOf("gif"),
        "Bitmap" to listOf("bmp", "dib"),
        "Wireless Bitmap" to listOf("wbmp"),
        "MS Windows Icon Format" to listOf("ico"),
        "Apple Icon Image" to listOf("icns"),
        "Interchange File Format" to listOf("iff"),
        "Joint Photographic Experts Group" to listOf("jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
        "Apple QuickDraw" to listOf("pict", "pct", "pic"),
        "Portable Any Map" to listOf("pnm", "ppm"),
        "Adobe Photoshop Document" to listOf("psd"),
        "Truevision TGA Image Format" to listOf("tga", "icb", "vda", "vst"),
        "Tagged Image File Format" to listOf("tiff", "tif"),
        "Google WebP Format" to listOf("webp"),
    )

    init {
        Exporter.registry[name] = this
        Importer.registry[name] = this
        Opener.registry[name] = this
    }

    override fun export(doc: RawkyDocument, file: File) {
        val frame = doc.children[0]
        val layers = frame.children
        val grid = frame.children[0].child

        val image = BufferedImage(grid.columns, grid.rows, BufferedImage.TYPE_INT_ARGB).apply {
            for (row in 0 until this.height) {
                for (column in 0 until this.width) {
                    for (layer in layers.reversed()) {
                        layer.child[column, row].colour.rgb.let { setRGB(column, row, it) }
                    }
                }
            }
        }

        ImageIO.write(image, file.extension, file)
    }

    override fun import(document: RawkyDocument, file: File) {
        ImageIO.read(file).apply {
            val frame = document.children[0]
            val layers = frame.children

            layers.add(
                0,
                Layer(
                    Grid(
                        rows = this.height,
                        columns = this.width
                    )
                )
            )

            for (row in 0 until this.height) {
                for (column in 0 until this.width) {
                    layers[0].child[column, row].colour = Color(getRGB(column, row), true)
                }
            }
        }
    }

    override fun open(file: File): RawkyDocument {
        val doc: RawkyDocument

        ImageIO.read(file).apply {
            doc = ActionUtil.newDocument(this.width, this.height, 1, 1)

            val frame = doc.children[0]
            val layers = frame.children

            for (row in 0 until this.height) {
                for (column in 0 until this.width) {
                    layers[0].child[column, row].colour = Color(getRGB(column, row), true)
                }
            }
        }

        return doc
    }
}
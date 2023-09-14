package com.deflatedpickle.rawky.pixelgrid.impex.imaging

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.CellProvider
import com.deflatedpickle.rawky.api.ColourChannel
import com.deflatedpickle.rawky.api.ImportAs
import com.deflatedpickle.rawky.api.impex.Exporter
import com.deflatedpickle.rawky.api.impex.Importer
import com.deflatedpickle.rawky.api.impex.Opener
import com.deflatedpickle.rawky.collection.Frame
import com.deflatedpickle.rawky.collection.Grid
import com.deflatedpickle.rawky.collection.Layer
import com.deflatedpickle.rawky.event.EventNewFrame
import com.deflatedpickle.rawky.event.EventNewLayer
import com.deflatedpickle.rawky.grid.pixel.PixelCellPlugin
import com.deflatedpickle.rawky.pixelgrid.impex.imageio.ImageIOPlugin
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.deflatedpickle.tosuto.api.ToastLevel
import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.Imaging
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Color
import java.awt.Desktop
import java.io.File

@Plugin(
    value = "imaging",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Export images supported by Apache Commons Imaging
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object ImagingPlugin : Exporter, Importer, Opener {
    override val name = "Imaging"
    override val exporterExtensions: MutableMap<String, List<String>> = mutableMapOf()
    override val importerExtensions: MutableMap<String, List<String>> = mutableMapOf()
    override val openerExtensions: MutableMap<String, List<String>> = mutableMapOf()

    init {
        Exporter.registry[name] = this
        Importer.registry[name] = this
        Opener.registry[name] = this

        for (i in listOf(exporterExtensions, importerExtensions, openerExtensions)) {
            i.putAll(
                mapOf(
                    "X BitMap" to listOf("xbm"),
                    "X PixMap" to listOf("xpm"),
                )
            )
        }
    }

    override fun export(doc: RawkyDocument, file: File) {
        Imaging.writeImage(
            ImageIOPlugin.frameToImage(doc, doc[doc.selectedIndex]),
            file,
            when (file.extension) {
                "xbm" -> ImageFormats.XBM
                "xpm" -> ImageFormats.XPM
                else -> throw NotImplementedError()
            }
        )

        Haruhi.toastWindow.add(
            ToastItem(
                level = ToastLevel.INFO,
                title = "Saved",
                // content = file.absolutePath,
                actions = listOf(
                    ToastSingleAction("Open") { _, toast: ToastItem ->
                        Desktop.getDesktop().open(file)
                        toast.close()
                    }
                )
            )
        )
    }

    override fun import(document: RawkyDocument, file: File, importAs: ImportAs) {
        if (CellProvider.current != PixelCellPlugin) {
            TaskDialogs.error(
                Haruhi.window,
                "Incompatible cell provider",
                "This file uses a cell provider other than the one currently loaded",
            )

            return
        }

        Imaging.getBufferedImage(file).apply {
            val frame = document[document.selectedIndex]
            val layers = frame.children

            val newLayer = Layer(child = Grid(rows = this.height, columns = this.width))
            val newFrame = Frame(children = mutableListOf(newLayer))

            val grid = when (importAs) {
                ImportAs.FRAMES -> {
                    document.children.add(newFrame)
                    EventNewFrame.trigger(newFrame)
                    newFrame[0].child
                }

                ImportAs.LAYERS -> {
                    layers.add(0, newLayer)
                    EventNewLayer.trigger(newLayer)
                    newLayer.child
                }
            }

            for (row in 0 until this.height) {
                for (column in 0 until this.width) {
                    grid[column, row].content = Color(getRGB(column, row), true)
                }
            }
        }
    }

    override fun open(file: File): RawkyDocument {
        CellProvider.current = PixelCellPlugin

        val image = Imaging.getBufferedImage(file)

        val doc = ActionUtil.newDocument(
            image.width,
            image.height,
            when (file.extension) {
                "xbm" -> ColourChannel.RGB
                "xpm" -> ColourChannel.ARGB
                else -> throw NotImplementedError()
            },
            1,
            1
        )

        image.apply {
            val frame = doc[doc.selectedIndex]
            val layers = frame.children

            for (row in 0 until this.height) {
                for (column in 0 until this.width) {
                    layers[0].child[column, row].content = Color(getRGB(column, row), true)
                }
            }
        }

        return doc
    }
}
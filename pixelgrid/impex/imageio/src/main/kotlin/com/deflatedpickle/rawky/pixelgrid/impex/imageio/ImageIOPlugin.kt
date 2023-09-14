/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.impex.imageio

import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.impl.IIOReadProgressAdapter
import com.deflatedpickle.marvin.impl.IIOWriteProgressAdapter
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
import com.deflatedpickle.rawky.pixelgrid.event.EventReadProgress
import com.deflatedpickle.rawky.pixelgrid.event.EventWriteProgress
import com.deflatedpickle.rawky.pixelgrid.event.PacketReadWriteProgress
import com.deflatedpickle.rawky.pixelgrid.impex.imageio.dialog.ExportImageDialog
import com.deflatedpickle.rawky.setting.RawkyDocument
import com.deflatedpickle.rawky.util.ActionUtil
import com.deflatedpickle.tosuto.TimedToastItem
import com.deflatedpickle.tosuto.ToastItem
import com.deflatedpickle.tosuto.action.ToastSingleAction
import com.deflatedpickle.tosuto.api.ToastLevel
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Color
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.ImageTypeSpecifier
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.FileImageInputStream
import javax.imageio.stream.FileImageOutputStream

@Plugin(
    value = "imageio",
    author = "DeflatedPickle",
    version = "1.0.2",
    description = """
        <br>
        Export images supported by ImageIO
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object ImageIOPlugin : Exporter, Importer, Opener {
    override val name = "ImageIO"

    override val exporterExtensions: MutableMap<String, List<String>> = mutableMapOf()
    override val importerExtensions: MutableMap<String, List<String>> = mutableMapOf()
    override val openerExtensions: MutableMap<String, List<String>> = mutableMapOf()

    init {
        Exporter.registry[name] = this
        Importer.registry[name] = this
        Opener.registry[name] = this

        // Add all the extensions with read/write support
        for (i in listOf(exporterExtensions, importerExtensions, openerExtensions)) {
            i.putAll(
                mapOf(
                    "Portable Network Graphics" to listOf("png"),
                    "Graphics Interchange Format" to listOf("gif"),
                    "Bitmap" to listOf("bmp", "dib"),
                    "Wireless Bitmap" to listOf("wbmp"),
                    "MS Windows Icon Format" to listOf("ico"),
                    "Apple Icon Image" to listOf("icns"),
                    "Interchange File Format" to listOf("iff"),
                    "Joint Photographic Experts Group" to
                            listOf("jpg", "jpeg", "jpe", "jif", "jfif", "jfi"),
                    "Apple QuickDraw" to listOf("pict", "pct", "pic"),
                    "NetPBM Portable Any Map" to listOf("pam"),
                    "NetPBM Portable Pix Map" to listOf("ppm"),
                    "Adobe Photoshop Document" to listOf("psd"),
                    "Truevision TGA Image Format" to listOf("tga", "icb", "vda", "vst"),
                    "Tagged Image File Format" to listOf("tif", "tiff"),
                    "BigTiff" to listOf("tif", "tiff", "btf", "tf8", "btiff"),
                    "Google WebP Format" to listOf("webp"),
                    "Quit Ok Image" to listOf("qoi"),
                ),
            )
        }

        // Add all extensions with only read support
        for (i in listOf(importerExtensions, openerExtensions)) {
            i.putAll(
                mapOf(
                    "Scalable Vector Graphics" to listOf("svg"),
                    "MS Windows Metafile" to listOf("wmf"),
                    "MS Cursor" to listOf("cur"),
                    "Radiance HDR" to listOf("hdr"),
                    "Lossless JPEG" to listOf("jpg", "ljpg", "ljpeg"),
                    "ZSoft Paintbrush" to listOf("pcx"),
                    "ZSoft Multi-Page Paintbrush" to listOf("dcx"),
                    "Apple MacPaint" to listOf("pntg"),
                    "NetPBM Portable Bit Map" to listOf("pbm"),
                    "NetPBM Portable Grey Map" to listOf("pgm"),
                    "NetPBM Portable Float Map" to listOf("pfm"),
                    "Adobe Photoshop Large Document" to listOf("psb"),
                    "Silicon Graphics Image" to listOf("sgi", "bw", "rgb", "rgba"),
                    "Windows Thumbnail Cache" to listOf("db"),
                    "X Window Dump" to listOf("xwd", "xdm"),
                ),
            )
        }
    }

    override fun export(doc: RawkyDocument, file: File) {
        val writer = getWriter(file)
        val parameters = writer.defaultWriteParam.apply {
            if (canWriteCompressed()) compressionMode = ImageWriteParam.MODE_EXPLICIT
            if (canWriteProgressive()) progressiveMode = ImageWriteParam.MODE_DEFAULT
        }
        val type = ImageTypeSpecifier.createFromBufferedImageType(doc.colourChannel.code)
        val metadata = writer.getDefaultImageMetadata(type, parameters)

        if (parameters.canWriteCompressed() || metadata != null) {
            val dialog = ExportImageDialog(parameters)
            dialog.isVisible = true

            if (dialog.result == TaskDialog.StandardCommand.OK) {
                parameters.compressionType = dialog.compressionType.selectedItem as String
                parameters.compressionQuality = dialog.compressionQualitySlider.value
            } else {
                return
            }
        }

        writer.output = FileImageOutputStream(file)

        if (writer.canWriteSequence()) {
            writer.prepareWriteSequence(metadata)

            for (i in doc.children) {
                val image = frameToImage(doc, i)
                writer.writeToSequence(IIOImage(image, listOf(image), null), parameters)
            }

            writer.endWriteSequence()
        } else {
            val image = frameToImage(doc, doc[doc.selectedIndex])

            writer.write(null, IIOImage(image, listOf(image), metadata), parameters)
        }

        (writer.output as FileImageOutputStream).close()

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

        val reader = getReader(file)
        reader.input = FileImageInputStream(file)
        reader.read(0).apply {
            val frame = document.children[document.selectedIndex]
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

        val reader = getReader(file)
        reader.input = FileImageInputStream(file)

        val numImages = reader.getNumImages(true)
        val doc = ActionUtil.newDocument(
            reader.getWidth(0),
            reader.getHeight(0),
            ColourChannel.get(reader.read(0).type),
            numImages,
            1
        )

        for (i in 0 until reader.getNumImages(true)) {
            reader.read(i).apply {
                val frame = doc.children[i]
                val layers = frame.children

                for (row in 0 until this.height) {
                    for (column in 0 until this.width) {
                        layers[0].child[column, row].content = Color(getRGB(column, row), true)
                    }
                }
            }
        }

        return doc
    }

    private fun getWriter(file: File) = ImageIO.getImageWritersByFormatName(file.extension).next().apply {
        addIIOWriteProgressListener(object : IIOWriteProgressAdapter() {
            override fun imageProgress(source: ImageWriter, percentageDone: Float) {
                EventWriteProgress.trigger(
                    PacketReadWriteProgress(
                        source = PluginUtil.slugToPlugin("DeflatedPickle@imageio#*")!!,
                        file = file,
                        progress = percentageDone
                    )
                )
            }
        })

        addIIOWriteWarningListener { _, _, warning ->
            Haruhi.toastWindow.add(
                TimedToastItem(
                    level = ToastLevel.WARNING,
                    title = "Image Write",
                    content = warning
                )
            )
        }
    }

    private fun getReader(file: File) =
        ImageIO.getImageReadersByFormatName(file.extension).next().apply {
            addIIOReadProgressListener(object : IIOReadProgressAdapter() {
                override fun imageProgress(source: ImageReader, percentageDone: Float) {
                    EventReadProgress.trigger(
                        PacketReadWriteProgress(
                            source = PluginUtil.slugToPlugin("DeflatedPickle@imageio#*")!!,
                            file = file,
                            progress = percentageDone
                        )
                    )
                }
            })

            addIIOReadWarningListener { _, warning ->
                Haruhi.toastWindow.add(
                    TimedToastItem(
                        level = ToastLevel.WARNING,
                        title = "Image Read",
                        content = warning
                    )
                )
            }
        }

    fun frameToImage(
        doc: RawkyDocument,
        frame: Frame
    ) = BufferedImage(doc[0][0].child.columns, doc[0][0].child.rows, doc.colourChannel.code).apply {
        for (row in 0 until this.height) {
            for (column in 0 until this.width) {
                for (layer in frame.children.reversed()) {
                    val color = (layer.child[column, row]
                        .content as Color)

                    if (layer.visible && layer.opacity > 0 && color.alpha > 0) {
                        setRGB(
                            column,
                            row,
                            color.rgb and (
                                    (layer.opacity * 255)
                                        .toInt() shl 24 or 0x00ffffff
                                    )
                        )
                    }
                }
            }
        }
    }
}

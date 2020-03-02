/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.ColourPalette
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.dialogue.New
import com.deflatedpickle.rawky.dialogue.ScaleImage
import com.deflatedpickle.rawky.jasc_pal.JASC_PALLexer
import com.deflatedpickle.rawky.jasc_pal.JASC_PALParser
import com.deflatedpickle.rawky.rexpaint_palette.RexPaint_PaletteLexer
import com.deflatedpickle.rawky.rexpaint_palette.RexPaint_PaletteParser
import com.deflatedpickle.rawky.util.extension.toConstantCase
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.icafe4j.image.gif.GIFTweaker
import com.icafe4j.image.reader.GIFReader
import java.awt.Color
import java.awt.Image
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.oxbow.swingbits.dialog.task.TaskDialog

object Commands {
    val fileChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("Rawky (*.rawr)", "rawr").also { this.fileFilter = it })
    }

    val imageChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png").also { this.fileFilter = it })
        addChoosableFileFilter(FileNameExtensionFilter("GIF (*.gif)", "gif"))
    }

    val pngChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png").also { this.fileFilter = it })
    }

    val gridChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("XML (*.xml)", "xml").also { this.fileFilter = it })
    }

    val gson = GsonBuilder().setPrettyPrinting().create()

    fun newDialog() {
        val dialog = New()

        when (dialog.show().tag) {
            TaskDialog.CommandTag.OK -> new(
                    dialog.widthSpinner.slider.value,
                    dialog.heightSpinner.slider.value
            )
        }
    }

    fun new(width: Int = 16, height: Int = 16, withFrame: Boolean = true) {
        PixelGrid.columnAmount = width
        PixelGrid.rowAmount = height
        PixelGrid.rectangleMatrix = PixelGrid.refreshMatrix()

        PixelGrid.frameList = mutableListOf()

        for (i in 0 until Components.layerList.tableModel.rowCount) {
            Components.layerList.tableModel.removeRow(i)
        }
        Components.animationTimeline.listModel.removeAllElements()

        if (withFrame) {
            Components.animationTimeline.addFrame()
        }
    }

    fun open() {
        if (fileChooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            new(withFrame = false)

            when (fileChooser.selectedFile.extension) {
                // TODO: Add support for more raw types, such as; PSD, PDN, MDP, XCF, etc
                "rawr" -> {
                    for ((key, value) in gson.fromJson(fileChooser.selectedFile.readText(), HashMap::class.java)) {
                        when (key) {
                            "frameList" -> {
                                for ((frameIndex, frame) in (value as ArrayList<*>).withIndex()) {
                                    Components.animationTimeline.addFrame(false)

                                    for ((layerIndex, layer) in ((frame as LinkedTreeMap<String, Any>)["layerList"] as ArrayList<*>).withIndex()) {
                                        val castLayer = layer as LinkedTreeMap<Any, Any>
                                        Components.layerList.addLayer()

                                        PixelGrid.frameList[frameIndex].layerList[layerIndex].apply {
                                            val rowList = mutableListOf<MutableList<PixelGrid.Cell>>()
                                            for (row in castLayer["pixelMatrix"] as MutableList<MutableList<Any>>) {
                                                val columnList = mutableListOf<PixelGrid.Cell>()
                                                for (column in row) {
                                                    // columnList.add(PixelGrid.Cell(this).apply {
                                                    //     if (!(column as LinkedTreeMap<String, LinkedTreeMap<Double, Double>>).isEmpty()) {
                                                    //         colour = (column["colour"] as LinkedTreeMap<String, Double>)["value"]?.toInt()?.let { Color(it) }!!
                                                    //     }
                                                    // })
                                                }
                                                rowList.add(columnList)
                                            }
                                            pixelMatrix = rowList
                                            visible = castLayer["visible"] as Boolean
                                            lockType = PixelGrid.Layer.LockType.valueOf(castLayer["lockType"] as String)
                                        }
                                    }
                                }
                            }
                            "colourLibrary" -> {
                                for (i in value as ArrayList<*>) {
                                    (i as LinkedTreeMap<String, Double>)["value"]?.toInt()?.let { Color(it) }?.let { Components.colourLibrary.addButton(it) }
                                }
                            }
                            "colourPalette" -> {
                                for (i in value as ArrayList<*>) {
                                    val ii = i as LinkedTreeMap<String, Int>
                                    ColourPalette.ColourSwatch(ii["x"]!!, ii["y"]!!, Color((ii["colour"] as LinkedTreeMap<String, Double>)["value"]!!.toInt()))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun save() {
        if (fileChooser.showSaveDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            when (fileChooser.selectedFile.extension) {
                // TODO: Add support for more raw types, such as; PSD, PDN, MDP, XCF, etc
                "rawr" -> {
                    fileChooser.selectedFile.writeText(gson.toJson(hashMapOf("frameList" to PixelGrid.frameList, "colourLibrary" to Components.colourLibrary.cellList.map { it.colour }, "colourPalette" to Components.colourPalette.colourList)))
                }
            }
        }
    }

    fun importJascPal() {
        val chooser = JFileChooser().apply {
            addChoosableFileFilter(FileNameExtensionFilter("JASC PAL (*.pal)", "pal").also { this.fileFilter = it })
        }

        if (chooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            val lexer = JASC_PALLexer(CharStreams.fromStream(chooser.selectedFile.inputStream()))
            val tokenStream = CommonTokenStream(lexer)
            val parser = JASC_PALParser(tokenStream)

            val startContext = parser.start()

            Components.colourLibrary.cellList.clear()
            for (i in startContext.rgb()) {
                Components.colourLibrary.addButton(Color(i.INT(0).text.toInt(), i.INT(1).text.toInt(), i.INT(2).text.toInt()))
            }
        }
    }

    fun importRexPaintPalette(component: EComponent) {
        val chooser = JFileChooser().apply {
            addChoosableFileFilter(FileNameExtensionFilter("RexPaint Palette (*.txt)", "txt").also { this.fileFilter = it })
        }

        if (chooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            val lexer = RexPaint_PaletteLexer(CharStreams.fromStream(chooser.selectedFile.inputStream()))
            val tokenStream = CommonTokenStream(lexer)
            val parser = RexPaint_PaletteParser(tokenStream)

            val startContext = parser.start()

            Components.colourLibrary.cellList.clear()
            for ((rowIndex, row) in startContext.row().withIndex()) {
                for ((columnIndex, i) in row.hex().withIndex()) {
                    when (component) {
                        EComponent.COLOUR_LIBRARY -> {
                            if (i.text != "#000000") {
                                Components.colourLibrary.addButton(Color.decode('#' + i.code.text))
                            }
                        }
                        EComponent.COLOUR_PALETTE -> {
                            ColourPalette.ColourSwatch(columnIndex * Components.colourPalette.cellSize, rowIndex * Components.colourPalette.cellSize, Color.decode('#' + i.code.text))
                        }
                        else -> return
                    }
                }

                for ((columnIndex, i) in row.rgb().withIndex()) {
                    when (component) {
                        EComponent.COLOUR_LIBRARY -> {
                            if (i.text != "{0,0,0}") {
                                Components.colourLibrary.addButton(Color(i.red.text.toInt(), i.green.text.toInt(), i.blue.text.toInt()))
                            }
                        }
                        EComponent.COLOUR_PALETTE -> {
                            ColourPalette.ColourSwatch(columnIndex * Components.colourPalette.cellSize, rowIndex * Components.colourPalette.cellSize, Color(i.red.text.toInt(), i.green.text.toInt(), i.blue.text.toInt()))
                        }
                        else -> return
                    }
                }
            }
        }
    }

    fun importImage() {
        if (imageChooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            when (imageChooser.selectedFile.extension) {
                "png" -> {
                    Components.animationTimeline.addFrame()
                    ImageIO.read(imageChooser.selectedFile).apply {
                        for (row in 0 until PixelGrid.rowAmount) {
                            for (column in 0 until PixelGrid.columnAmount) {
                                PixelGrid.frameList[0].layerList[0].pixelMatrix[column][row].colour = Color(getRGB(column, row), true)
                            }
                        }
                    }
                }
                "gif" -> {
                    GIFReader().apply {
                        read(imageChooser.selectedFile.inputStream())

                        PixelGrid.frameList.clear()
                        for ((frameIndex, frame) in frames.withIndex()) {
                            Components.animationTimeline.addFrame()
                            for (row in 0 until frame.height) {
                                for (column in 0 until frame.width) {
                                    PixelGrid.frameList[frameIndex].layerList[0].pixelMatrix[column][row].colour = Color(frame.getRGB(column, row), true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun png(imageType: Int = BufferedImage.TYPE_INT_ARGB): BufferedImage =
            BufferedImage(PixelGrid.columnAmount, PixelGrid.rowAmount, imageType).apply {
                for (row in 0 until this.height) {
                    for (column in 0 until this.width) {
                        for (layer in PixelGrid.frameList[0].layerList.reversed()) {
                            layer.pixelMatrix[column][row].colour.rgb.let { setRGB(column, row, it) }
                        }
                    }
                }
            }

    fun exportImage(width: Int = PixelGrid.columnAmount, height: Int = PixelGrid.rowAmount, imageType: Int = BufferedImage.TYPE_INT_ARGB, scaleHints: Int = Image.SCALE_FAST) {
        if (imageChooser.showSaveDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            when (imageChooser.selectedFile.extension) {
                "png" -> {
                    ImageIO.write(
                            png(imageType).apply { getScaledInstance(width, height, scaleHints) },
                            "png", imageChooser.selectedFile
                    )
                }
                "gif" -> {
                    val frameList = mutableListOf<BufferedImage>()
                    for (frame in PixelGrid.frameList) {
                        BufferedImage(PixelGrid.columnAmount, PixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                            for (layer in frame.layerList.reversed()) {
                                for (row in 0 until PixelGrid.rowAmount) {
                                    for (column in 0 until PixelGrid.columnAmount) {
                                        layer.pixelMatrix[row][column].colour.rgb.let {
                                            setRGB(column, row, it)
                                        }
                                    }
                                }
                            }
                            frameList.add(this)
                        }
                    }
                    // TODO: Add an option for delay
                    GIFTweaker.writeAnimatedGIF(frameList.toTypedArray(), IntArray(PixelGrid.frameList.size) { 1000 / 60 }, imageChooser.selectedFile.outputStream())
                }
            }
        }
    }

    fun scaledImage() {
        val dialog = ScaleImage()

        if (dialog.show().tag == TaskDialog.CommandTag.OK) {
            if (pngChooser.showSaveDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
                when (pngChooser.selectedFile.extension) {
                    "png" -> {
                        val oldImage = png()
                        val newImage = BufferedImage(
                                oldImage.width * dialog.widthSlider.slider.value,
                                oldImage.height * dialog.heightSlider.slider.value,
                                BufferedImage.TYPE_INT_ARGB
                        )

                        val affineTransform = AffineTransform.getScaleInstance(
                                dialog.widthSlider.slider.value.toDouble(),
                                dialog.heightSlider.slider.value.toDouble()
                        )

                        val affineTransformOp = AffineTransformOp(affineTransform,
                                ScaleImage.ScaleType
                                        .valueOf((dialog.scaleCombobox.model.selectedItem as String)
                                                .toConstantCase()).type)

                        ImageIO.write(
                                affineTransformOp.filter(oldImage, newImage),
                                "png", pngChooser.selectedFile
                        )
                    }
                }
            }
        }
    }

    fun importGridLayout() {
        if (gridChooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            when (gridChooser.selectedFile.extension) {
                "xml" -> {
                    Components.cControl.readXML(gridChooser.selectedFile)
                }
            }
        }
    }

    fun exportGridLayout() {
        if (gridChooser.showSaveDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            when (gridChooser.selectedFile.extension) {
                "xml" -> {
                    Components.cControl.writeXML(gridChooser.selectedFile)
                }
            }
        }
    }
}

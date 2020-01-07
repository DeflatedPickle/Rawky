/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import com.deflatedpickle.rawky.component.ColourPalette
import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.jasc_pal.JASC_PALLexer
import com.deflatedpickle.rawky.jasc_pal.JASC_PALParser
import com.deflatedpickle.rawky.rexpaint_palette.RexPaint_PaletteLexer
import com.deflatedpickle.rawky.rexpaint_palette.RexPaint_PaletteParser
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.icafe4j.image.gif.GIFTweaker
import com.icafe4j.image.reader.GIFReader
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

object Commands {
    val fileChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("Rawky (*.rawr)", "rawr").also { this.fileFilter = it })
        addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png"))
        addChoosableFileFilter(FileNameExtensionFilter("GIF (*.gif)", "gif"))
    }

    val gson = GsonBuilder().setPrettyPrinting().create()

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
                                                    columnList.add(PixelGrid.Cell(this).apply {
                                                        if (!(column as LinkedTreeMap<String, LinkedTreeMap<Double, Double>>).isEmpty()) {
                                                            colour = (column["colour"] as LinkedTreeMap<String, Double>)["value"]?.toInt()?.let { Color(it) }
                                                        }
                                                    })
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
                "png" -> {
                    Components.animationTimeline.addFrame()
                    ImageIO.read(fileChooser.selectedFile).apply {
                        for (row in 0 until PixelGrid.rowAmount) {
                            for (column in 0 until PixelGrid.columnAmount) {
                                PixelGrid.frameList[0].layerList[0].pixelMatrix[row][column].colour = Color(getRGB(column, row), true)
                            }
                        }
                    }
                }
                "gif" -> {
                    GIFReader().apply {
                        read(fileChooser.selectedFile.inputStream())

                        PixelGrid.frameList.clear()
                        for ((frameIndex, frame) in frames.withIndex()) {
                            Components.animationTimeline.addFrame()
                            for (row in 0 until frame.height) {
                                for (column in 0 until frame.width) {
                                    PixelGrid.frameList[frameIndex].layerList[0].pixelMatrix[row][column].colour = Color(frame.getRGB(column, row), true)
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
                "rawr" -> {
                    fileChooser.selectedFile.writeText(gson.toJson(hashMapOf("frameList" to PixelGrid.frameList, "colourLibrary" to Components.colourLibrary.cellList.map { it.colour }, "colourPalette" to Components.colourPalette.colourList)))
                }
                // TODO: Move finished files to an export sub-menu of the file menu, this should only be used for raw file types
                // TODO: Add support for more raw types, such as; PSD, PDN, MDP, XCF, etc
                "png" -> {
                    ImageIO.write(BufferedImage(PixelGrid.columnAmount, PixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                        for (row in 0 until PixelGrid.rowAmount) {
                            for (column in 0 until PixelGrid.columnAmount) {
                                for (layer in PixelGrid.frameList[0].layerList.reversed()) {
                                    layer.pixelMatrix[row][column].colour?.rgb?.let { setRGB(column, row, it) }
                                }
                            }
                        }
                    }, "png", fileChooser.selectedFile)
                }
                "gif" -> {
                    val frameList = mutableListOf<BufferedImage>()
                    for (frame in PixelGrid.frameList) {
                        BufferedImage(PixelGrid.columnAmount, PixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                            for (layer in frame.layerList.reversed()) {
                                for (row in 0 until PixelGrid.rowAmount) {
                                    for (column in 0 until PixelGrid.columnAmount) {
                                        layer.pixelMatrix[row][column].colour?.rgb?.let {
                                            setRGB(column, row, it)
                                        }
                                    }
                                }
                            }
                            frameList.add(this)
                        }
                    }
                    // TODO: Add an option for delay
                    GIFTweaker.writeAnimatedGIF(frameList.toTypedArray(), IntArray(PixelGrid.frameList.size) { 1000 / 60 }, fileChooser.selectedFile.outputStream())
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
}

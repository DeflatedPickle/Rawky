package com.deflatedpickle.rawky.utils

import com.deflatedpickle.rawky.components.PixelGrid
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.icafe4j.image.gif.GIFTweaker
import com.icafe4j.image.reader.GIFReader
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object Commands {
    val fileChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("Rawky (*.rawr)", "rawr").also { this.fileFilter = it })
        addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png"))
        addChoosableFileFilter(FileNameExtensionFilter("GIF (*.gif)", "gif"))
    }

    val gson = Gson()

    fun new(withFrame: Boolean = true) {
        for ((frameIndex, frame) in Components.pixelGrid.frameList.withIndex()) {
            Components.pixelGrid.frameList[frameIndex].layerList = mutableListOf()
        }
        Components.pixelGrid.frameList = mutableListOf()

        for (i in 0 until Components.layerList.listModel.rowCount) {
            Components.layerList.listModel.removeRow(i)
        }
        Components.animationTimeline.listModel.removeAllElements()

        if (withFrame) {
            Components.animationTimeline.addFrame()
        }
    }

    fun open() {
        if (fileChooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            new(false)

            when (fileChooser.selectedFile.extension) {
                "rawr" -> {
                    // FIXME: Only loads 1 frame and doesn't populate the frame list or layer table
                    for (key in gson.fromJson(fileChooser.selectedFile.readText(), MutableList::class.java)) {
                        for ((frameIndex, frame) in (key as LinkedTreeMap<*, *>).toList().withIndex()) {
                            // Components.animationTimeline.listModel.addElement("Frame ${Components.animationTimeline.listModel.size()}")

                            for ((layerIndex, layer) in (frame.second as ArrayList<*>).withIndex()) {
                                // Components.layerList.listModel.insertRow(0, arrayOf(null, "Layer ${Components.layerList.listModel.rowCount}", true, false))

                                val castLayer = layer as LinkedTreeMap<Any, Any>
                                Components.pixelGrid.frameList[frameIndex].layerList[layerIndex].apply {
                                    val rowList = mutableListOf<MutableList<PixelGrid.Cell>>()
                                    for (row in castLayer["pixelMatrix"] as MutableList<MutableList<Any>>) {
                                        val columnList = mutableListOf<PixelGrid.Cell>()
                                        for (column in row) {
                                            columnList.add(PixelGrid.Cell().apply {
                                                if (!(column as LinkedTreeMap<String, LinkedTreeMap<Double, Double>>).isEmpty()) {
                                                    colour = (column["colour"] as LinkedTreeMap<String, Double>)["value"]?.toInt()?.let { Color(it) }
                                                }
                                            })
                                        }
                                        rowList.add(columnList)
                                    }
                                    pixelMatrix = rowList
                                    visible = castLayer["visible"] as Boolean
                                    locked = castLayer["locked"] as Boolean
                                }
                            }
                        }
                    }
                }
                "png" -> {
                    Components.animationTimeline.addFrame()
                    ImageIO.read(fileChooser.selectedFile).apply {
                        for (row in 0 until Components.pixelGrid.rowAmount) {
                            for (column in 0 until Components.pixelGrid.columnAmount) {
                                Components.pixelGrid.frameList[0].layerList[0].pixelMatrix[row][column].colour = Color(getRGB(row, column), true)
                            }
                        }
                    }
                }
                "gif" -> {
                    GIFReader().apply {
                        read(fileChooser.selectedFile.inputStream())

                        Components.pixelGrid.frameList.clear()
                        for ((frameIndex, frame) in frames.withIndex()) {
                            Components.animationTimeline.addFrame()
                            for (row in 0 until frame.height) {
                                for (column in 0 until frame.width) {
                                    Components.pixelGrid.frameList[frameIndex].layerList[0].pixelMatrix[row][column].colour = Color(frame.getRGB(row, column), true)
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
                    fileChooser.selectedFile.writeText(gson.toJson(Components.pixelGrid.frameList))
                }
                "png" -> {
                    ImageIO.write(BufferedImage(Components.pixelGrid.columnAmount, Components.pixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                        for (row in 0 until Components.pixelGrid.rowAmount) {
                            for (column in 0 until Components.pixelGrid.columnAmount) {
                                for (layer in Components.pixelGrid.frameList[0].layerList.reversed()) {
                                    layer.pixelMatrix[row][column].colour?.rgb?.let { setRGB(row, column, it) }
                                }
                            }
                        }
                    }, "png", fileChooser.selectedFile)
                }
                "gif" -> {
                    val frameList = mutableListOf<BufferedImage>()
                    for (frame in Components.pixelGrid.frameList) {
                        BufferedImage(Components.pixelGrid.columnAmount, Components.pixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                            for (layer in frame.layerList.reversed()) {
                                for (row in 0 until Components.pixelGrid.rowAmount) {
                                    for (column in 0 until Components.pixelGrid.columnAmount) {
                                        layer.pixelMatrix[row][column].colour?.rgb?.let {
                                            setRGB(row, column, it)
                                        }
                                    }
                                }
                            }
                            frameList.add(this)
                        }
                    }
                    // TODO: Add an option for delay
                    GIFTweaker.writeAnimatedGIF(frameList.toTypedArray(), IntArray(Components.pixelGrid.frameList.size) { 1000 / 60 }, fileChooser.selectedFile.outputStream())
                }
            }
        }
    }
}
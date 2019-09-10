package com.deflatedpickle.rawky.utils

import com.deflatedpickle.rawky.components.PixelGrid
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object Commands {
    val fileChooser = JFileChooser().apply {
        addChoosableFileFilter(FileNameExtensionFilter("Rawky (*.rawr)", "rawr").also { this.fileFilter = it })
        addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png"))
    }

    val gson = Gson()

    fun new() {
        for ((frameIndex, frame) in Components.pixelGrid.frameList.withIndex()) {
            Components.pixelGrid.frameList[frameIndex].layerList = mutableListOf()
        }
        Components.pixelGrid.frameList = mutableListOf()

        for (i in 0 until Components.layerList.listModel.rowCount) {
            Components.layerList.listModel.removeRow(i)
        }
        Components.animationTimeline.listModel.removeAllElements()

        Components.animationTimeline.addFrame()
    }

    fun open() {
        if (fileChooser.showOpenDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            new()

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
                                for (frame in Components.pixelGrid.frameList) {
                                    for (layer in frame.layerList.reversed()) {
                                        layer.pixelMatrix[row][column].colour?.rgb?.let { setRGB(row, column, it) }
                                    }
                                }
                            }
                        }
                    }, "png", fileChooser.selectedFile)
                }
            }
        }
    }
}
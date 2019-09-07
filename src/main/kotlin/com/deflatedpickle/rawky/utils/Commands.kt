package com.deflatedpickle.rawky.utils

import com.deflatedpickle.rawky.components.PixelGrid
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object Commands {
    fun save() {
        val fileChooser = JFileChooser().apply {
            addChoosableFileFilter(FileNameExtensionFilter("PNG (*.png)", "png").also { this.fileFilter = it })
        }
        if (fileChooser.showSaveDialog(Components.frame) == JFileChooser.APPROVE_OPTION) {
            ImageIO.write(BufferedImage(Components.pixelGrid.columnAmount, Components.pixelGrid.rowAmount, BufferedImage.TYPE_INT_ARGB).apply {
                for (row in 0 until Components.pixelGrid.rowAmount) {
                    for (column in 0 until Components.pixelGrid.columnAmount) {
                        for (layer in Components.pixelGrid.layerList.reversed()) {
                            layer.pixelMatrix[row][column].colour?.rgb?.let { setRGB(row, column, it) }
                        }
                    }
                }
            }, "png", fileChooser.selectedFile)
        }
    }
}
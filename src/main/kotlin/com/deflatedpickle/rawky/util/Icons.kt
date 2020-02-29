/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util

import java.awt.Dimension
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object Icons {
    val initialSize = Dimension(24, 24)

    val createNew = icon("paper_new")
    val openedFolder = icon("folder_open")
    val picture = icon("save_as")

    val cut = icon("cut")
    val copy = icon("copy")
    val paste = icon("paste")
    val trash = icon("delete")

    val boxSelect = icon("box_select")
    val move = icon("move")

    val settings = icon("settings")

    val crop = icon("crop")

    val undo = icon("undo")
    val redo = icon("redo")

    val pencil = icon("pencil")
    val eraser = icon("eraser")
    val colourPicker = icon("colour_picker")
    val bucket = icon("paint_bucket")

    val arrow = icon("arrow")

    val zoomIn = icon("zoom_in")
    val zoomOut = icon("zoom_out")

    val lock = icon("lock_locked")
    val unlock = icon("lock_unlocked")

    val hide = icon("hide")
    val show = icon("show")

    val addElement = icon("add_element")

    val backArrow = icon("back_arrow")
    val forwardArrow = icon("forward_arrow")

    private fun icon(name: String): ImageIcon {
        return ImageIcon(ImageIO.read(ClassLoader.getSystemResource("friable/icons/$name.png")))
    }
}

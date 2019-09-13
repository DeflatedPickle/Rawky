package com.deflatedpickle.rawky.util

import java.awt.Dimension
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object Icons {
    val initialSize = Dimension(24, 24)

    val create_new = icon("paper_new")
    val opened_folder = icon("folder_open")
    val picture = icon("save_as")

    val cut = icon("cut")
    val copy = icon("copy")
    val paste = icon("paste")
    val trash = icon("delete")

    val settings = icon("settings")

    val crop = icon("crop")

    val undo = icon("undo")

    val pencil = icon("pencil")
    val eraser = icon("eraser")
    val colour_picker = icon("colour_picker")

    val zoom_in = icon("zoom_in")
    val zoom_out = icon("zoom_out")

    val lock = icon("lock_locked")
    val unlock = icon("lock_unlocked")

    val hide = icon("hide")
    val show = icon("show")

    val add_element = icon("add_element")
    
    fun icon(name: String): ImageIcon {
        return ImageIcon(ImageIO.read(ClassLoader.getSystemResource("friable/icons/$name.png")))
    }
}
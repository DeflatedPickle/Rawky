package com.deflatedpickle.rawky.utils

import org.pushingpixels.photon.icon.SvgBatikResizableIcon
import java.awt.Dimension

object Icons {
    val initialSize = Dimension(24, 24)

    val file = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/file.svg"), initialSize)

    val create_new = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/create_new.svg"), initialSize)
    val opened_folder = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/opened_folder.svg"), initialSize)
    val picture = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/picture.svg"), initialSize)

    val cut = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/cut.svg"), initialSize)
    val copy = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/copy.svg"), initialSize)
    val paste = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/paste.svg"), initialSize)
    val trash = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/trash.svg"), initialSize)

    val crop = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/crop.svg"), initialSize)

    val undo = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/undo.svg"), initialSize)

    val pencil = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/pencil.svg"), initialSize)
    val eraser = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/eraser.svg"), initialSize)
    val colour_picker = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/color_dropper.svg"), initialSize)

    val plus = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/plus.svg"), initialSize)
    val minus = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/minus.svg"), initialSize)

    val lock = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/lock_2.svg"), initialSize)
    val unlock = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/unlock_2.svg"), initialSize)

    val rounded_rectangle = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/rounded_rectangle.svg"), initialSize)
    val rounded_rectangle_filled = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/rounded_rectangle_filled.svg"), initialSize)

    val settings = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/settings.svg"), initialSize)
}
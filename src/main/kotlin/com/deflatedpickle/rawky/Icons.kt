package com.deflatedpickle.rawky

import org.pushingpixels.photon.icon.SvgBatikResizableIcon
import java.awt.Dimension

object Icons {
    val initialSize = Dimension(24, 24)

    val pencil = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/pencil.svg"), initialSize)
    val eraser = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/eraser.svg"), initialSize)
    val colour_picker = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/color_dropper.svg"), initialSize)

    val plus = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/plus.svg"), initialSize)
    val minus = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/minus.svg"), initialSize)

    val lock = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/lock_2.svg"), initialSize)
    val unlock = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/unlock_2.svg"), initialSize)

    val rounded_rectangle = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/rounded_rectangle.svg"), initialSize)
    val rounded_rectangle_filled = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/rounded_rectangle_filled.svg"), initialSize)
}
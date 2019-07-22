package com.deflatedpickle.rawky

import org.pushingpixels.photon.icon.SvgBatikResizableIcon
import java.awt.Dimension

object Icons {
    val initialSize = Dimension(24, 24)

    val pencil = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/pencil.svg"), initialSize)
    val eraser = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/eraser.svg"), initialSize)
    val plus = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/plus.svg"), initialSize)
    val colour_picker = SvgBatikResizableIcon.getSvgIcon(ClassLoader.getSystemResource("icons/svg/production/color_dropper.svg"), initialSize)
}
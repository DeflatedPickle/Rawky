/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.widget

import com.bric.colorpicker.ColorPickerDialog
import com.deflatedpickle.rawky.util.Components
import java.awt.Color
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter

class ColourButton(var color: Color) : JXButton() {
    val mattePainter = MattePainter(color)
    val compoundPainter = CompoundPainter<JXButton>(mattePainter)

    init {
        backgroundPainter = compoundPainter

        addActionListener {
            color = ColorPickerDialog.showDialog(Components.frame, color)
            mattePainter.fillPaint = color
        }
    }
}

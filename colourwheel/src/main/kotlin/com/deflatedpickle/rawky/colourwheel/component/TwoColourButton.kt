@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.rawky.colourwheel.component

import com.bric.colorpicker.ColorPickerDialog
import com.deflatedpickle.haruhi.Haruhi
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.rawky.event.EventChangeColour
import com.deflatedpickle.rawky.pixelcell.PixelCellPlugin
import com.deflatedpickle.undulation.functions.ColourButton
import com.deflatedpickle.undulation.functions.JButton
import com.deflatedpickle.undulation.widget.ColourButton
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeEvent

class TwoColourButton(
    p: Color = Color.BLACK,
    s: Color = Color.WHITE,
) : JPanel() {
    var primary = p
        set(value) {
            field = value
            primaryButton.colour = value
        }

    var secondary = s
        set(value) {
            field = value
            secondaryButton.colour = value
        }

    private val primaryButton = DoubleClickButton(primary) {
        PixelCellPlugin.current = it.colour
    }
    private val secondaryButton = DoubleClickButton(secondary) {
        PixelCellPlugin.current = it.colour
    }

    private val switchButton = JButton(icon = MonoIcon.SWAP) {
        val c = secondaryButton.colour

        secondaryButton.colour = primaryButton.colour
        primaryButton.colour = c

        EventChangeColour.trigger(c)
    }

    init {
        layout = null

        val b = 4
        preferredSize = Dimension(64, 128)

        primaryButton.bounds = Rectangle(b, b, 32, 32)
        secondaryButton.bounds = Rectangle(b + 16, b + 16, 32, 32)

        switchButton.bounds = Rectangle(16, b * 2 + 48, 32, 32)

        add(primaryButton)
        add(secondaryButton)
        add(switchButton)
    }
}
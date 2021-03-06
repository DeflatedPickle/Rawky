/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.util.ColourAPI
import com.deflatedpickle.rawky.util.Components
import com.deflatedpickle.rawky.util.UsefulValues
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.JSlider
import javax.swing.UIManager
import javax.swing.border.Border
import javax.swing.border.LineBorder
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.TransferHandler

class ColourShades : Component() {
    val shadesSlider = JSlider(3, 3 * 31).apply {
        value = amount

        addChangeListener {
            Components.colourShades.amount = this.value
            Components.colourShades.createShades()
            Components.colourShades.updateShades()
            Components.colourShades.repaint()
        }
    }

    // TODO: Split the amount for darker and lighter shades
    var amount = 14
    val buttonSize = Dimension(40, 20)
    val buttonList = mutableListOf<JXButton>()

    val cachedBorder: Border

    lateinit var selectedButton: JXButton

    init {
        toolbarWidgets[BorderLayout.PAGE_END] = listOf(
                Pair(shadesSlider, null)
        )

        this.layout = WrapLayout(WrapLayout.LEFT, 0, 0)

        createShades()
        cachedBorder = buttonList[0].border
    }

    fun createShades() {
        removeAll()
        buttonList.clear()

        val shades = getShades()
        for (i in 0 until amount) {
            this.add(JXButton().apply {
                preferredSize = buttonSize
                foreground = shades[i]
                backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[i]))

                if (i == amount / 2 + 1) {
                    UsefulValues.currentColour = shades[i]
                    selectedButton = this
                }

                addActionListener {
                    val colour = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
                    UsefulValues.currentColour = colour
                    selectedButton = this

                    for (b in buttonList) {
                        b.border = cachedBorder
                    }

                    border = LineBorder(UIManager.getColor("List.selectionBackground"), 2)
                }

                addMouseListener(object : MouseAdapter() {
                    override fun mouseEntered(e: MouseEvent) {
                        val colour = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color

                        ColourTransfer(colour).pressedExport(this@apply)

                        toolTipText = ColourAPI.id(colour).getJSONObject("name").getString("value")
                    }
                })

                buttonList.add(this)
            })
        }
    }

    fun updateShades() {
        val shades = Components.colourShades.getShades()
        for ((index, button) in Components.colourShades.buttonList.withIndex()) {
            button.backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[index]))
        }
        Components.colourShades.selectedButton.actionListeners[0].actionPerformed(ActionEvent(Components.colourShades.selectedButton, 0, ""))
        Components.colourShades.selectedButton.border = cachedBorder
    }

    fun getShades(): List<Color> {
        val list = mutableListOf<Color>()
        for (i in (0 until amount / 2).reversed()) {
            list.add(darken(UsefulValues.currentColour, i / (amount.toDouble() / 2) + 0.1))
        }
        list.add(UsefulValues.currentColour)
        for (i in 0 until amount / 2) {
            list.add(lighten(UsefulValues.currentColour, i / (amount.toDouble() / 2) + 0.1))
        }
        return list
    }

    // https://stackoverflow.com/a/18605575
    fun darken(color: Color, fraction: Double): Color {
        return Color(
                max(0.0, color.red - 255 * fraction).roundToInt(),
                max(0.0, color.green - 255 * fraction).roundToInt(),
                max(0.0, color.blue - 255 * fraction).roundToInt(),
                color.alpha
        )
    }

    fun lighten(color: Color, fraction: Double): Color {
        return Color(
                min(255.0, color.red + 255 * fraction).roundToInt(),
                min(255.0, color.green + 255 * fraction).roundToInt(),
                min(255.0, color.blue + 255 * fraction).roundToInt(),
                color.alpha
        )
    }
}

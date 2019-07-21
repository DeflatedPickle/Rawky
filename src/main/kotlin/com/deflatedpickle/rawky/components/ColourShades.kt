package com.deflatedpickle.rawky.components

import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ColourShades : JPanel() {
    // TODO: Split the amount for darker and lighter shades
    var amount = 7
    val buttonList = mutableListOf<JXButton>()

    var colour = Color.BLACK
        set(value) {
            field = value

            val shades = getShades()
            for ((index, button) in this.buttonList.withIndex()) {
                button.backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[index]))
            }
            this.selectedButton.actionListeners[0].actionPerformed(ActionEvent(this.selectedButton, 0, ""))
            this.selectedButton.text = " "
        }

    var selectedShade: Color? = null
    lateinit var selectedButton: JXButton

    init {
        this.layout = GridBagLayout()

        val shades = getShades()
        for (i in 0 until amount) {
            this.add(JXButton().apply {
                buttonList.add(this)
                foreground = Color.BLACK
                backgroundPainter = CompoundPainter<JXButton>(MattePainter(shades[i]))

                if (i == amount / 2 + 1) {
                    selectedButton = this
                }

                addActionListener {
                    selectedShade = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
                    selectedButton = this

                    for (b in buttonList) {
                        b.text = ""
                    }

                    text = " "
                }
            }, GridBagConstraints().apply {
                fill = GridBagConstraints.BOTH
                weightx = 1.0
                weighty = 1.0
            })
        }
    }

    fun getShades(): List<Color> {
        val list = mutableListOf<Color>()
        for (i in (0 until amount / 2).reversed()) {
            list.add(darken(colour, i / (amount.toDouble() / 2) + 0.1))
        }
        list.add(colour)
        for (i in 0 until amount / 2) {
            list.add(lighten(colour, i / (amount.toDouble() / 2) + 0.1))
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
package com.deflatedpickle.rawky.component

import com.deflatedpickle.rawky.api.annotations.IntOpt
import com.deflatedpickle.rawky.api.annotations.Options
import com.deflatedpickle.rawky.api.annotations.Tooltip
import com.deflatedpickle.rawky.api.component.Component
import com.deflatedpickle.rawky.transfer.ColourTransfer
import com.deflatedpickle.rawky.util.ColourAPI
import com.deflatedpickle.rawky.util.Components
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.painter.CompoundPainter
import org.jdesktop.swingx.painter.MattePainter
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

object ColourHistory : Component() {
    @Options
    object Settings {
        @IntOpt(1, 400)
        @Tooltip("Change the amount of colours cached")
        @JvmField
        var cacheSize = 20
    }

    class Cell(val colour: Color, val button: JXButton)

    var cellSize = 20
    var cellList = mutableSetOf<ColourLibrary.Cell>()

    init {
        this.layout = WrapLayout()
    }

    fun addButton(colour: Color) {
        while (cellList.size >= Settings.cacheSize) {
            cellList = cellList.drop(1).toMutableSet()
            remove(0)
        }

        add(JXButton().apply {
            val cell = ColourLibrary.Cell(colour, this)
            cellList.add(cell)
            preferredSize = Dimension(cellSize, cellSize)
            backgroundPainter = CompoundPainter<JXButton>(MattePainter(colour))

            addActionListener {
                Components.colourPicker.color = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
            }

            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    val color = ((backgroundPainter as CompoundPainter<JXButton>).painters[0] as MattePainter).fillPaint as Color
                    toolTipText = ColourAPI.id(color).getJSONObject("name").getString("value")
                }
            })

            ColourTransfer(colour).pressedExport(this)
        }, this@ColourHistory.components.indexOf(this))

        this.repaint()
    }
}
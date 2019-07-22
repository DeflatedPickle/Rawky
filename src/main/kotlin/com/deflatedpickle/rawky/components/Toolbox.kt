package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.Icons
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Dimension
import javax.swing.AbstractButton
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    enum class Tool {
        PENCIL,
        ERASER,
        PICKER
    }
    var tool = Tool.PENCIL

    val pencilButton = JToggleButton(Icons.pencil).apply {
        preferredSize = dimension
        toolTipText = "Pencil"
        addActionListener { tool = Tool.PENCIL }
    }
    val eraserButton = JToggleButton(Icons.eraser).apply {
        preferredSize = dimension
        toolTipText = "Eraser"
        addActionListener { tool = Tool.ERASER }
    }
    val pickerButton = JToggleButton(Icons.colour_picker).apply {
        preferredSize = dimension
        toolTipText = "Colour Picker"
        addActionListener { tool = Tool.PICKER }
    }

    init {
        this.layout = WrapLayout()

        this.add(pencilButton)
        this.add(eraserButton)
        this.add(pickerButton)

        val buttonGroup = ButtonGroup()
        for (i in components) {
            buttonGroup.add(i as AbstractButton)
        }
    }
}
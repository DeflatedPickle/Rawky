package com.deflatedpickle.rawky.components

import com.deflatedpickle.rawky.Icons
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.Dimension
import javax.swing.ButtonGroup
import javax.swing.JPanel
import javax.swing.JToggleButton

class Toolbox : JPanel() {
    val dimension = Dimension(28, 28)

    enum class Tool {
        PENCIL,
        ERASER
    }
    var tool = Tool.PENCIL

    val pencilButton = JToggleButton(Icons.pencil).apply {
        preferredSize = dimension
        addActionListener { tool = Tool.PENCIL }
    }
    val eraserButton = JToggleButton(Icons.eraser).apply {
        preferredSize = dimension
        addActionListener { tool = Tool.ERASER }
    }

    init {
        this.layout = WrapLayout()

        this.add(pencilButton)
        this.add(eraserButton)

        val buttonGroup = ButtonGroup()
        buttonGroup.add(pencilButton)
        buttonGroup.add(eraserButton)
    }
}
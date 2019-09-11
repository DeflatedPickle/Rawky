package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Components
import java.awt.*
import javax.swing.*

class New : JDialog(Components.frame, "New File", true) {
    val widthSpinner = JSpinner(SpinnerNumberModel(16, 0, Int.MAX_VALUE, 16))
    val heightSpinner = JSpinner(SpinnerNumberModel(16, 0, Int.MAX_VALUE, 16))

    val labelConstraints = GridBagConstraints().apply {
        anchor = GridBagConstraints.EAST
    }

    val lineEnd = GridBagConstraints().apply {
        gridwidth = GridBagConstraints.REMAINDER
        insets = Insets(2, 2, 2, 2)
    }

    init {
        layout = BorderLayout()
        size = Dimension(240, 160)

        add(JPanel(GridBagLayout()).apply {
            add(JLabel("Width:"), labelConstraints)
            add(widthSpinner, lineEnd)

            add(JLabel("Height:"), labelConstraints)
            add(heightSpinner, lineEnd)
        })

        add(JPanel().apply {
            add(JButton("OK").apply {
                addActionListener {
                    Commands.new(widthSpinner.value as Int, heightSpinner.value as Int)
                    this@New.dispose()
                }
            })
            add(JButton("Cancel").apply { addActionListener { this@New.dispose() } })
        }, BorderLayout.PAGE_END)
    }
}
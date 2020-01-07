/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.dialogue

import com.deflatedpickle.rawky.component.PixelGrid
import com.deflatedpickle.rawky.util.Commands
import com.deflatedpickle.rawky.util.Components
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.SwingUtilities

class New : JDialog(Components.frame, "New File", true) {
    val widthSpinner = JSpinner(SpinnerNumberModel(PixelGrid.columnAmount, 0, Int.MAX_VALUE, 16))
    val heightSpinner = JSpinner(SpinnerNumberModel(PixelGrid.rowAmount, 0, Int.MAX_VALUE, 16))

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
                SwingUtilities.invokeLater { rootPane.defaultButton = this }

                addActionListener {
                    Commands.new(widthSpinner.value as Int, heightSpinner.value as Int)
                    this@New.dispose()
                }
            })
            add(JButton("Cancel").apply { addActionListener { this@New.dispose() } })
        }, BorderLayout.PAGE_END)
    }
}

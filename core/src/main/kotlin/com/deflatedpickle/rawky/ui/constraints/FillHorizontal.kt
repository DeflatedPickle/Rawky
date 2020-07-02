package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints

object FillHorizontal : GridBagConstraints() {
    init {
        anchor = NORTH
        fill = HORIZONTAL
        weightx = 1.0
    }
}

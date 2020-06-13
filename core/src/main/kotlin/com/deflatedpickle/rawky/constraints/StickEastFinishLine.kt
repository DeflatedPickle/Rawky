package com.deflatedpickle.rawky.constraints

import java.awt.GridBagConstraints

object StickEastFinishLine : GridBagConstraints() {
    init {
        anchor = EAST
        gridwidth = REMAINDER
    }
}
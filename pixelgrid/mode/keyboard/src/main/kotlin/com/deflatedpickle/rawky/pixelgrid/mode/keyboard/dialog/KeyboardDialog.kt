/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.mode.keyboard.dialog

import com.deflatedpickle.haruhi.Haruhi
import org.oxbow.swingbits.dialog.task.TaskDialog
import so.madprogrammer.keyboard.Keyboard
import javax.swing.JToggleButton

class KeyboardDialog : TaskDialog(Haruhi.window, "Keyboard") {
    val keyboard = Keyboard(::JToggleButton)

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = keyboard
    }
}

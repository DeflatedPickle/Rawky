package com.deflatedpickle.rawky.pixelgrid.mode.keyboard.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import org.oxbow.swingbits.dialog.task.TaskDialog
import so.madprogrammer.keyboard.Keyboard
import javax.swing.JToggleButton

class KeyboardDialog : TaskDialog(PluginUtil.window, "Keyboard") {
    val keyboard = Keyboard(::JToggleButton)

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = keyboard
    }
}
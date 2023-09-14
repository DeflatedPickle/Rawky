package com.deflatedpickle.rawky.util

import com.deflatedpickle.haruhi.event.EventCreateDocument
import com.deflatedpickle.haruhi.event.EventOpenDocument
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.RawkyPlugin
import com.deflatedpickle.rawky.event.EventUpdateGrid
import com.deflatedpickle.undulation.functions.JMenuItem
import java.awt.event.KeyEvent
import javax.swing.JMenuItem
import javax.swing.KeyStroke

object CommonMenuItems {
    private val disabledUntilFile = mutableListOf<JMenuItem>()

    fun undoItem() = JMenuItem(
        "Undo",
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
        message = "Revoke the last action",
        enabled = false,
    ) {
        RawkyPlugin.document?.let { doc ->
            ActionStack.undo()

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            EventUpdateGrid.trigger(layer.child)
        }
    }.also { disabledUntilFile.add(it) }

    fun redoItem() = JMenuItem(
        "Redo",
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK),
        message = "Perform the last revoked action",
        enabled = false,
    ) {
        RawkyPlugin.document?.let { doc ->
            ActionStack.redo()

            val frame = doc.children[doc.selectedIndex]
            val layer = frame.children[frame.selectedIndex]

            EventUpdateGrid.trigger(layer.child)
        }
    }.also { disabledUntilFile.add(it) }

    init {
        EventProgramFinishSetup.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = RawkyPlugin.document != null
            }
        }

        EventCreateDocument.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = true
            }
        }

        EventOpenDocument.addListener {
            for (i in disabledUntilFile) {
                i.isEnabled = true
            }
        }
    }
}
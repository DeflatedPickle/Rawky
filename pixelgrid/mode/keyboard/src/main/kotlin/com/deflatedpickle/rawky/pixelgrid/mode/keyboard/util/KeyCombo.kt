package com.deflatedpickle.rawky.pixelgrid.mode.keyboard.util

import java.awt.event.KeyEvent


data class KeyCombo(
    val key: Int,
    val modifier: Int? = null,
) {
    fun accept(event: KeyEvent) =
        if (event.keyCode == key) {
            if (modifier == null) {
                true
            } else {
                event.modifiersEx and when (modifier) {
                    KeyEvent.VK_SHIFT -> KeyEvent.SHIFT_DOWN_MASK
                    KeyEvent.VK_CONTROL -> KeyEvent.CTRL_DOWN_MASK
                    KeyEvent.VK_ALT -> KeyEvent.ALT_DOWN_MASK
                    KeyEvent.VK_ALT_GRAPH -> KeyEvent.ALT_GRAPH_DOWN_MASK
                    else -> 0
                } != 0
            }
        } else {
            false
        }
}
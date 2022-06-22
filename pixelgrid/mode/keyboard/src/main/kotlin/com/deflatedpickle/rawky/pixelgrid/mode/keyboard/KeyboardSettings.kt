package com.deflatedpickle.rawky.pixelgrid.mode.keyboard

import com.deflatedpickle.rawky.settings.api.IntRange
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.awt.event.KeyEvent.VK_ALT_GRAPH
import java.awt.event.KeyEvent.VK_DOWN
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_LEFT
import java.awt.event.KeyEvent.VK_RIGHT
import java.awt.event.KeyEvent.VK_UP

@Serializable
data class KeyboardSettings(
    @Required @IntRange(VK_ENTER, VK_ALT_GRAPH) var negativeY: Int = VK_UP,
    @Required @IntRange(VK_ENTER, VK_ALT_GRAPH) var positiveY: Int = VK_DOWN,
    @Required @IntRange(VK_ENTER, VK_ALT_GRAPH) var negativeX: Int = VK_LEFT,
    @Required @IntRange(VK_ENTER, VK_ALT_GRAPH) var positiveX: Int = VK_RIGHT,
    @Required @IntRange(VK_ENTER, VK_ALT_GRAPH) var useTool: Int = VK_ENTER,
)

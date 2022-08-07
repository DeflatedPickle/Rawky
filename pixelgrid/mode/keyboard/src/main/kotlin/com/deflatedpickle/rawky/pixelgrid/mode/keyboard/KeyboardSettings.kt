package com.deflatedpickle.rawky.pixelgrid.mode.keyboard

import com.deflatedpickle.haruhi.api.Config
import com.deflatedpickle.rawky.settings.api.range.IntRange
import kotlinx.serialization.Serializable
import java.awt.event.KeyEvent.VK_ALT_GRAPH
import java.awt.event.KeyEvent.VK_DOWN
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_LEFT
import java.awt.event.KeyEvent.VK_RIGHT
import java.awt.event.KeyEvent.VK_UP

@Serializable
data class KeyboardSettings(
    override val version: Int = 1,
    @IntRange(VK_ENTER, VK_ALT_GRAPH) var negativeY: Int = VK_UP,
    @IntRange(VK_ENTER, VK_ALT_GRAPH) var positiveY: Int = VK_DOWN,
    @IntRange(VK_ENTER, VK_ALT_GRAPH) var negativeX: Int = VK_LEFT,
    @IntRange(VK_ENTER, VK_ALT_GRAPH) var positiveX: Int = VK_RIGHT,
    @IntRange(VK_ENTER, VK_ALT_GRAPH) var useTool: Int = VK_ENTER,
): Config

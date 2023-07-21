package com.deflatedpickle.rawky.pixelgrid.mode.gamepad

import net.java.games.input.Component

data class GamePadEvent(
    val identifier: Component.Identifier,
    val data: Float,
)
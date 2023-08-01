/* Copyright (c) 2023 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.pixelgrid.mode.gamepad

import net.java.games.input.Component

data class GamePadEvent(
    val identifier: Component.Identifier,
    val data: Float,
)

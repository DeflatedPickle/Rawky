/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.colourpalette.api

import java.awt.Color

data class Palette(
    val name: String,
    val colours: Map<Color, String?>,
) {
    override fun toString() = name
}

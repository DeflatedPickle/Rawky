/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.palette

data class Palette<T>(
    val name: String,
    val items: Map<T, String?>,
) {
    override fun toString() = name
}

/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused", "SpellCheckingInspection")

package com.deflatedpickle.rawky.api

import java.awt.image.BufferedImage

enum class ColourChannel(
    val code: Int,
) {
    RGB(BufferedImage.TYPE_INT_RGB),
    ARGB(BufferedImage.TYPE_INT_ARGB),
    BGR(BufferedImage.TYPE_3BYTE_BGR),
    ABGR(BufferedImage.TYPE_4BYTE_ABGR),
    GRAY(BufferedImage.TYPE_BYTE_GRAY),
    BINARY(BufferedImage.TYPE_BYTE_BINARY),
    INDEXED(BufferedImage.TYPE_BYTE_INDEXED),
    ;

    companion object {
        fun get(code: Int) = ColourChannel.values().first { it.code == code }
    }
}

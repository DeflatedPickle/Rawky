/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.server.backend.util.functions.extension

operator fun ByteArray.get(intRange: IntRange): ByteArray =
    ByteArray(intRange.last - intRange.first) {
        this[intRange.first + it]
    }

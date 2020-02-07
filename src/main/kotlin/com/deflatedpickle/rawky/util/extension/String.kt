/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util.extension

fun String.fromEnum() =
    this.toLowerCase()
            .split("_")
            .joinToString(" ") { it.capitalize() }

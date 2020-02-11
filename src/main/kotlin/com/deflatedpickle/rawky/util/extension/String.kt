/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.util.extension

import org.apache.commons.lang3.StringUtils

fun String.toCamelCase(separator: String = " ") =
        this.toLowerCase()
                .split("_")
                .joinToString(separator) { it.capitalize() }

fun String.toConstantCase(separator: String = "_") =
        this.toUpperCase()
                .split(" ")
                .joinToString(separator)

fun String.fromCamelCaseToConstantCase(separator: String = "_"): String {
    // Credit (axtaxt): https://stackoverflow.com/a/3752693
    return this.split(Regex("(?=\\p{Upper})"))
            .filter { it != StringUtils.EMPTY }
            .joinToString(separator) { it.toUpperCase() }
}

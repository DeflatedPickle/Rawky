/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.api.template

import com.deflatedpickle.marvin.registry.Registry
import kotlinx.serialization.Serializable

@Serializable
data class Template(
    val name: String,
    val width: Int,
    val height: Int,
    val guides: List<String> = listOf(),
) {
    companion object {
        val registry = Registry<String, Template>()
    }

    override fun toString() = name
}

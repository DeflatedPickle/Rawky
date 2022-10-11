package com.deflatedpickle.rawky.api.template

import com.deflatedpickle.marvin.registry.Registry
import kotlinx.serialization.Serializable

@Serializable
data class Guide(
    val name: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
) {
    companion object {
        val registry = Registry<String, List<Guide>>()
    }

    override fun toString() = name
}
